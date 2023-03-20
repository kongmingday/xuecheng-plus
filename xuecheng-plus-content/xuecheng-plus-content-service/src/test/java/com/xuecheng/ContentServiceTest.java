package com.xuecheng;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.config.MultipartSupportConfig;
import com.xuecheng.content.feignclient.MediaServiceClient;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.service.CourseBaseInfoService;
import com.xuecheng.content.service.CourseCategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

/**
 * @author eotouch
 * @date 2023/01/21
 */
@SpringBootTest
public class ContentServiceTest {

    @Autowired
    private CourseBaseMapper courseBaseMapper;

    @Autowired
    private CourseBaseInfoService courseBaseInfoService;

    @Autowired
    private CourseCategoryService courseCategoryService;

    @Test
    void courseBase(){
        CourseBase courseBase = courseBaseMapper.selectById(22);
        System.out.println(courseBase);
    }

    @Test
    void courseService(){
        PageResult<CourseBase> courseBasePageResult = courseBaseInfoService.queryCourseBaseList(new PageParams(), new QueryCourseParamsDto());
        System.out.println(courseBasePageResult);
    }

    @Test
    void courseCategoryService(){
        List<CourseCategoryTreeDto> courseCategoryTreeDtos = courseCategoryService.queryTreeNodes("1");
        System.out.println(courseCategoryTreeDtos);
    }

    @Autowired
    MediaServiceClient mediaServiceClient;

    //远程调用，上传文件
    @Test
    public void test() {

        MultipartFile multipartFile = MultipartSupportConfig.getMultipartFile(new File("D:\\TempData\\1.html"));
        mediaServiceClient.upload(multipartFile,"course","test.html");
    }

}
