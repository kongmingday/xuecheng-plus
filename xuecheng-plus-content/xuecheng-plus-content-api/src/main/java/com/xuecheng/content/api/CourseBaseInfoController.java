package com.xuecheng.content.api;

import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.*;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.service.CourseBaseInfoService;
import com.xuecheng.content.service.CourseMarketService;
import com.xuecheng.content.util.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author eotouch
 * @date 2023/01/21
 */
@Api(value = "课程管理相关接口", tags = "课程管理相关接口")
@RestController
@Slf4j
public class CourseBaseInfoController {

    @Autowired
    private CourseBaseInfoService courseBaseInfoService;

    @Autowired
    private CourseMarketService courseMarketService;

    @ApiOperation("课程查询接口")
    @PostMapping("/course/list")
    @PreAuthorize("hasAuthority('xc_teachmanager_course_list')")
    public PageResult<CourseBase> list(PageParams params, @RequestBody QueryCourseParamsDto queryCourseParamsDto){
        Long companyId = getCompanyId();
        return courseBaseInfoService.queryCourseBaseList(companyId, params,queryCourseParamsDto);
    }

    @ApiOperation("新增课程基础信息")
    @PostMapping("/course")
    @PreAuthorize("hasAuthority('xc_teachmanager_course_list')")
    public CourseBaseInfoDto createCourseBase(@RequestBody @Validated AddCourseDto addCourseDto){
        Long companyId = getCompanyId();
        return courseBaseInfoService.createCourseBase(companyId, addCourseDto);
    }

    @ApiOperation("根据课程id查询课程基础信息")
    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAuthority('xc_teachmanager_course_list')")
    public CourseBaseInfoDto getCourseBaseById(@PathVariable Long courseId){
        return courseBaseInfoService.getCourseBaseInfo(courseId);
    }

    @ApiOperation("修改课程基础信息")
    @PutMapping("/course")
    @PreAuthorize("hasAuthority('xc_teachmanager_course_list')")
    public CourseBaseInfoDto modifyCourseBase(@RequestBody EditCourseDto editCourseDto){
        Long companyId = getCompanyId();
        return courseBaseInfoService.updateCourseBase(companyId, editCourseDto);
    }

    @ApiOperation("删除课程基础信息")
    @DeleteMapping("/course/{courseId}")
    @PreAuthorize("hasAuthority('xc_teachmanager_course_list')")
    public boolean deleteCourseBaseById(@PathVariable Long courseId){
        return courseBaseInfoService.deleteBaseInfo(courseId);
    }


    private Long getCompanyId(){
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        if (user == null){
            XueChengPlusException.cast("登录失效");
        }
        Long companyId = Long.parseLong(user.getCompanyId());
        return companyId;
    }
}
