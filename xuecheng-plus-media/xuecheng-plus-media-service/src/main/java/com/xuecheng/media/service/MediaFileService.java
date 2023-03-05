package com.xuecheng.media.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.File;
import java.util.List;

/**
 * @author eotouch
 * @version 1.0
 * @description 媒资文件管理业务类
 * @date 2022/9/10 8:55
 */
public interface MediaFileService {

    /**
     * @param pageParams          分页参数
     * @param queryMediaParamsDto 查询条件
     * @return com.xuecheng.base.model.PageResult<com.xuecheng.media.model.po.MediaFiles>
     * @description 媒资文件查询方法
     * @author eotouch
     * @date 2022/9/10 8:57
     */
    PageResult<MediaFiles> queryMediaFiels(Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto);

    /**
     * @description 文件上传
     * @param companyId 公司id
     * @param uploadFileParamsDto 上传文件参数
     * @param folder 文件目录
     * @param objectName 文件名
     * @return com.xuecheng.media.model.dto.UploadFileResultDto 上传结果
     * @author eotouch
     * @date 2023-03-03 23:03
     */
    UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, byte[] bytes, String folder, String objectName);


    /**
     * @description 添加内容到minio
     * @param bytes 字节数组
     * @param bucket minio桶
     * @param objectName 文件名
     * @param contentType 文件类型
     * @return void
     * @author eotouch
     * @date 2023-03-04 0:21
     */
    void addMediaFilesToMinIO(byte[] bytes, String bucket, String objectName, String contentType);

    /**
     * @description 添加文件信息到数据库
     * @param companyId 公司名称
     * @param uploadFileParamsDto 上传文件参数
     * @param objectName 文件名
     * @return com.xuecheng.media.model.po.MediaFiles
     * @author eotouch
     * @date 2023-03-04 0:21
     */
    MediaFiles addMediaFilesToDb(Long companyId,String fileMd5,UploadFileParamsDto uploadFileParamsDto,String bucket,String objectName);

    /**
     * @description 检查文件是否存在
     * @param fileMd5 文件的md5
     * @return com.xuecheng.base.model.RestResponse<java.lang.Boolean> false不存在，true存在
     * @author eotouch
     * @date 2023-03-04 0:21
     */
    RestResponse<Boolean> checkFile(String fileMd5);


    /**
     * @description 检查分块是否存在
     * @param fileMd5 文件MD5值
     * @param chunkIndex 分块序号
     * @return com.xuecheng.base.model.RestResponse<java.lang.Boolean> false不存在, true存在
     * @author eotouch
     * @date 2023-03-04 17:31
     */
    RestResponse<Boolean> checkChunk(String fileMd5, int chunkIndex);


    /**
     * @description 上传分块
     * @param fileMd5  文件md5
     * @param chunk  分块序号
     * @param bytes  文件字节
     * @return com.xuecheng.base.model.RestResponse
     * @author eotouch
     * @date 2023-03-04 17:31
     */
    RestResponse uploadChunk(String fileMd5,int chunk,byte[] bytes);


    /**
     * @description 合并分块文件
     * @param companyId 公司id
     * @param fileMd5 文件MD5
     * @param chunkTotal 分块总数
     * @param uploadFileParamsDto 上传文件参数
     * @return com.xuecheng.base.model.RestResponse
     * @author eotouch
     * @date 2023-03-04 23:31
     */
    RestResponse mergechunks(Long companyId, String fileMd5, int chunkTotal, UploadFileParamsDto uploadFileParamsDto);


    /**
     * @description 根据文件id查询文件信息
     * @param id 文件id
     * @return com.xuecheng.media.model.po.MediaFiles
     * @author eotouch
     * @date 2023-03-05 13:10
     */
    MediaFiles getFileById(String id);

    /**
     * @description 根据bucket和object下载Mino上指定文件
     * @param file 指定文件对象
     * @param bucket 桶名
     * @param objectName 对象名
     * @return java.io.File
     * @author eotouch
     * @date 2023-03-05 18:31
     */
    File downloadFileFromMinIO(File file, String bucket, String objectName);

    /**
     * @description 添加媒体资源到Minio
     * @param filePath 文件路径
     * @param bucket 桶名
     * @param objectName 文件名称
     * @return void
     * @author eotouch
     * @date 2023-03-05 18:32
     */
    void addMediaFilesToMinIO(String filePath, String bucket, String objectName);
}
