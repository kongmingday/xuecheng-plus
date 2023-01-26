package com.xuecheng;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.service.CourseBaseInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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


    @Test
    void contentLoad(){
        CourseBase courseBase = courseBaseMapper.selectById(22);
        System.out.println(courseBase);
    }

    @Test
    void courseService(){
        PageResult<CourseBase> courseBasePageResult = courseBaseInfoService.queryCourseBaseList(new PageParams(), new QueryCourseParamsDto());
        System.out.println(courseBasePageResult);
    }
}
