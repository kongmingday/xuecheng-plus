package com.xuecheng.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.media.mapper.MediaFilesMapper;
import com.xuecheng.media.mapper.MediaProcessMapper;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.model.po.MediaProcess;
import com.xuecheng.media.service.MediaFileService;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.UploadObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author eotouch
 * @version 1.0
 * @description TODO
 * @date 2022/9/10 8:58
 */
@Service
@Slf4j
public class MediaFileServiceImpl implements MediaFileService {

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private MediaFilesMapper mediaFilesMapper;

    @Autowired
    private MediaFileService currentProxy;

    @Autowired
    private MediaProcessMapper mediaProcessMapper;

    //普通文件桶
    @Value("${minio.bucket.files}")
    private String bucketFiles;

    @Value("${minio.bucket.videofiles}")
    private String bucket_videoFiles;

    @Override
    public PageResult<MediaFiles> queryMediaFiels(Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto) {

        //构建查询条件对象
        LambdaQueryWrapper<MediaFiles> queryWrapper = new LambdaQueryWrapper<>();
        String filename = queryMediaParamsDto.getFilename();
        String fileType = queryMediaParamsDto.getFileType();
        queryWrapper.like(!StringUtils.isEmpty(filename), MediaFiles::getFilename, filename);
        queryWrapper.eq(!StringUtils.isEmpty(fileType), MediaFiles::getFileType, fileType);

        //分页对象
        Page<MediaFiles> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        // 查询数据内容获得结果
        Page<MediaFiles> pageResult = mediaFilesMapper.selectPage(page, queryWrapper);
        // 获取数据列表
        List<MediaFiles> list = pageResult.getRecords();
        // 获取数据总数
        long total = pageResult.getTotal();
        // 构建结果集
        PageResult<MediaFiles> mediaListResult = new PageResult<>(list, total, pageParams.getPageNo(), pageParams.getPageSize());
        return mediaListResult;

    }

    @Override
    public UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, byte[] bytes, String folder, String objectName) {

        // 解析文件后缀名
        String contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;


        // 如果无文件名, 生成文件
        if (StringUtils.isEmpty(folder)) {
            folder = getFileFolder(new Date(), true, true, true);
        } else if (folder.indexOf("/") < 0) {
            folder += "/";
        }

        String fileMd5 = DigestUtils.md5Hex(bytes);
        String fileName = uploadFileParamsDto.getFilename();
        if (StringUtils.isEmpty(objectName)) {
            objectName = fileMd5 + fileName.substring(fileName.lastIndexOf("."));
        }
        objectName = folder + objectName;


        if (objectName.contains(".")) {
            String extension = objectName.substring(objectName.lastIndexOf("."));
            ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(extension);
            if (extensionMatch != null) {
                contentType = extensionMatch.getMimeType();
            }
        }
        MediaFiles mediaFiles = null;
        try {
            currentProxy.addMediaFilesToMinIO(bytes, bucketFiles, objectName, contentType);
            mediaFiles = currentProxy.addMediaFilesToDb(companyId, fileMd5, uploadFileParamsDto, bucketFiles, objectName);
            UploadFileResultDto uploadFileResultDto = new UploadFileResultDto();
            BeanUtils.copyProperties(mediaFiles, uploadFileResultDto);
            return uploadFileResultDto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }

    }

    @Transactional
    public MediaFiles addMediaFilesToDb(Long companyId, String fileMd5, UploadFileParamsDto uploadFileParamsDto, String bucket, String objectName) {

        // 查找数据库内文件数据，无则添加新信息
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
        if (mediaFiles == null) {
            mediaFiles = new MediaFiles();
            BeanUtils.copyProperties(uploadFileParamsDto, mediaFiles);
            mediaFiles.setId(fileMd5);
            mediaFiles.setFileId(fileMd5);
            mediaFiles.setCompanyId(companyId);
            mediaFiles.setUrl("/" + bucket + "/" + objectName);
            mediaFiles.setFilePath(objectName);
            mediaFiles.setBucket(bucket);
            mediaFiles.setCreateDate(LocalDateTime.now());
            mediaFiles.setStatus("1");


            //保存文件信息到文件表
            int insert = mediaFilesMapper.insert(mediaFiles);
            if (insert < 0) {
                XueChengPlusException.cast("保存文件信息失败");
            }

            String extension = null;
            String filename = uploadFileParamsDto.getFilename();
            if(StringUtils.isNotEmpty(filename) && filename.indexOf(".")>=0){
                extension = filename.substring(filename.lastIndexOf("."));
            }
            //媒体类型
            String mimeType = getMimeTypeByextension(extension);
            //图片、mp4视频可以设置URL
            if(mimeType.contains("image") || mimeType.contains("mp4")){
                mediaFiles.setUrl("/" + bucket + "/" + objectName);
            }

            if (mimeType.equals("video/x-msvideo")){
                MediaProcess mediaProcess = new MediaProcess();
                BeanUtils.copyProperties(mediaFiles, mediaProcess);

                mediaProcess.setStatus("1");
                mediaProcessMapper.insert(mediaProcess);
            }
        }
        return mediaFiles;
    }

    @Override
    public RestResponse<Boolean> checkFile(String fileMd5) {
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
        if (mediaFiles != null) {
            //桶
            String bucket = mediaFiles.getBucket();
            //存储目录
            String filePath = mediaFiles.getFilePath();
            //文件流
            InputStream stream = null;
            try {
                stream = minioClient.getObject(
                        GetObjectArgs.builder()
                                .bucket(bucket)
                                .object(filePath)
                                .build());

                if (stream != null) {
                    //文件已存在
                    return RestResponse.success(true);
                }
            } catch (Exception e) {
                return RestResponse.success(false);
            }
        }
        //文件不存在
        return RestResponse.success(false);
    }

    @Override
    public RestResponse<Boolean> checkChunk(String fileMd5, int chunkIndex) {
        //得到分块文件目录
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        //得到分块文件的路径
        String chunkFilePath = chunkFileFolderPath + chunkIndex;

        //文件流
        InputStream fileInputStream = null;
        try {
            fileInputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket_videoFiles)
                            .object(chunkFilePath)
                            .build());

            if (fileInputStream != null) {
                //分块已存在
                return RestResponse.success(true);
            }
        } catch (Exception e) {
            return RestResponse.success(false);
        }
        //分块未存在
        return RestResponse.success(false);

    }

    @Override
    public RestResponse uploadChunk(String fileMd5, int chunk, byte[] bytes) {
        //得到分块文件的目录路径
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        //得到分块文件的路径
        String chunkFilePath = chunkFileFolderPath + chunk;

        try {
            //将文件存储至minIO
            addMediaFilesToMinIO(bytes, bucket_videoFiles, chunkFilePath, "application/octet-stream");

        } catch (Exception ex) {
            ex.printStackTrace();
            XueChengPlusException.cast("上传过程出错请重试");
        }
        return RestResponse.success();
    }

    @Override
    public RestResponse mergechunks(Long companyId, String fileMd5, int chunkTotal, UploadFileParamsDto uploadFileParamsDto) {
        String fileName = uploadFileParamsDto.getFilename();
        //下载所有分块文件
        File[] chunkFiles = checkChunkStatus(fileMd5, chunkTotal);
        //扩展名
        String extName = fileName.substring(fileName.lastIndexOf("."));
        //创建临时文件作为合并文件
        File mergeFile = null;
        try {
            mergeFile = File.createTempFile(fileMd5, extName);
        } catch (IOException e) {
            XueChengPlusException.cast("合并文件过程中创建临时文件出错");
        }

        try {
            //开始合并
            byte[] b = new byte[1024];
            try (RandomAccessFile raf_write = new RandomAccessFile(mergeFile, "rw");) {
                for (File chunkFile : chunkFiles) {
                    try (FileInputStream chunkFileStream = new FileInputStream(chunkFile);) {
                        int len = -1;
                        while ((len = chunkFileStream.read(b)) != -1) {
                            //向合并后的文件写
                            raf_write.write(b, 0, len);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                XueChengPlusException.cast("合并文件过程中出错");
            }
            log.debug("合并文件完成{}", mergeFile.getAbsolutePath());
            uploadFileParamsDto.setFileSize(mergeFile.length());

            try (InputStream mergeFileInputStream = new FileInputStream(mergeFile);) {
                //对文件进行校验，通过比较md5值
                String newFileMd5 = DigestUtils.md5Hex(mergeFileInputStream);
                if (!fileMd5.equalsIgnoreCase(newFileMd5)) {
                    //校验失败
                    XueChengPlusException.cast("合并文件校验失败");
                }
                log.debug("合并文件校验通过{}", mergeFile.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
                //校验失败
                XueChengPlusException.cast("合并文件校验异常");
            }

            //将临时文件上传至minio
            String mergeFilePath = getFilePathByMd5(fileMd5, extName);
            try {

                //上传文件到minIO
                addMediaFilesToMinIO(mergeFile.getAbsolutePath(), bucket_videoFiles, mergeFilePath);
                log.debug("合并文件上传MinIO完成{}", mergeFile.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
                XueChengPlusException.cast("合并文件时上传文件出错");
            }

            //入数据库
            MediaFiles mediaFiles = addMediaFilesToDb(companyId, fileMd5, uploadFileParamsDto, bucket_videoFiles, mergeFilePath);
            if (mediaFiles == null) {
                XueChengPlusException.cast("媒资文件入库出错");
            }

            return RestResponse.success();
        } finally {
            //删除临时文件
            for (File file : chunkFiles) {
                try {
                    file.delete();
                } catch (Exception e) {

                }
            }
            try {
                mergeFile.delete();
            } catch (Exception e) {

            }
        }

    }

    @Transactional
    public void addMediaFilesToMinIO(byte[] bytes, String bucket, String objectName, String contentType) {

        // 上传文件到minio
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            PutObjectArgs build = PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .stream(byteArrayInputStream, byteArrayInputStream.available(), -1)
                    .contentType(contentType)
                    .build();
            minioClient.putObject(build);
        } catch (Exception e) {
            e.printStackTrace();
            XueChengPlusException.cast("上传过程中出错");
        }
    }

    public void addMediaFilesToMinIO(String filePath, String bucket, String objectName) {
        try {
            minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .filename(filePath)
                            .build());
        } catch (Exception e) {
            e.printStackTrace();
            XueChengPlusException.cast("上传文件到文件系统出错");
        }
    }

    //根据桶和文件路径从minio下载文件
    public File downloadFileFromMinIO(File file, String bucket, String objectName) {
        InputStream fileInputStream = null;
        OutputStream fileOutputStream = null;
        try {
            fileInputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .build());
            try {
                fileOutputStream = new FileOutputStream(file);
                IOUtils.copy(fileInputStream, fileOutputStream);

            } catch (IOException e) {
                XueChengPlusException.cast("下载文件" + objectName + "出错");
            }
        } catch (Exception e) {
            e.printStackTrace();
            XueChengPlusException.cast("文件不存在" + objectName);
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return file;
    }

    @Override
    public MediaFiles getFileById(String id) {
        return mediaFilesMapper.selectById(id);
    }

    private String getFilePathByMd5(String fileMd5, String fileExt) {
        return fileMd5.charAt(0) + "/" + fileMd5.charAt(1) + "/" + fileMd5 + "/" + fileMd5 + fileExt;
    }

    //根据扩展名拿匹配的媒体类型
    private String getMimeTypeByextension(String extension){
        //资源的媒体类型
        String contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;//默认未知二进制流
        if(StringUtils.isNotEmpty(extension)){
            ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(extension);
            if (extensionMatch != null) {
                contentType = extensionMatch.getMimeType();
            }
        }
        return  contentType;
    }


    private String getFileFolder(Date date, boolean year, boolean month, boolean day) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //获取当前日期字符串
        String dateString = sdf.format(new Date());
        //取出年、月、日
        String[] dateStringArray = dateString.split("-");
        StringBuffer folderString = new StringBuffer();
        if (year) {
            folderString.append(dateStringArray[0]);
            folderString.append("/");
        }
        if (month) {
            folderString.append(dateStringArray[1]);
            folderString.append("/");
        }
        if (day) {
            folderString.append(dateStringArray[2]);
            folderString.append("/");
        }
        return folderString.toString();
    }

    private String getChunkFileFolderPath(String fileMd5) {
        return fileMd5.charAt(0) + "/" + fileMd5.charAt(1) + "/" + fileMd5 + "/" + "chunk" + "/";
    }

    private File[] checkChunkStatus(String fileMd5, int chunkTotal) {
        //得到分块文件的目录路径
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        File[] files = new File[chunkTotal];
        //检查分块文件是否上传完毕
        for (int i = 0; i < chunkTotal; i++) {
            String chunkFilePath = chunkFileFolderPath + i;
            //下载文件
            File chunkFile = null;
            try {
                chunkFile = File.createTempFile("chunk" + i, null);
            } catch (IOException e) {
                e.printStackTrace();
                XueChengPlusException.cast("下载分块时创建临时文件出错");
            }
            downloadFileFromMinIO(chunkFile, bucket_videoFiles, chunkFilePath);
            files[i] = chunkFile;
        }
        return files;
    }


}
