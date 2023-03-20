package com.xuecheng.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;

/***
 * @description 课程管理接口Service
 * @author eotouch
 * @date 2023-01-22 21:05
 */
public interface CourseBaseInfoService extends IService<CourseBase> {

    /***
     * @description 课程查询
     * @param params 分页参数
     * @param queryCourseParamsDto 查询条件
     * @param companyId 企业id
     * @return com.xuecheng.base.model.PageResult<com.xuecheng.content.model.po.CourseBase>
     * @author eotouch
     * @date 2023-01-22 16:29
     */
    PageResult<CourseBase> queryCourseBaseList(Long companyId, PageParams params, QueryCourseParamsDto queryCourseParamsDto);

    /**
     * @description 添加课程基本信息
     * @param companyId 教学机构id
     * @param addCourseDto 课程基本信息
     * @return com.xuecheng.content.model.dto.CourseBaseInfoDto
     * @author eotouch
     * @date 2023-01-26 22:03
     */
    CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto addCourseDto);

    /**
     * @description 查询指定课程基本信息
     * @param courseId 课程id
     * @return com.xuecheng.content.model.dto.CourseBaseInfoDto
     * @author eotouch
     * @date 2023-02-01 22:34
     */
    CourseBaseInfoDto getCourseBaseInfo(long courseId);

    /**
     * @description 修改课程基本信息
     * @param companyId 教学机构id
     * @param dto 修改信息
     * @return com.xuecheng.content.model.dto.CourseBaseInfoDto
     * @author eotouch
     * @date 2023-02-01 22:36
     */
    CourseBaseInfoDto updateCourseBase(Long companyId, EditCourseDto dto);

    /**
     * @description 删除指定id课程信息
     * @param courseId 课程id
     * @return java.lang.Boolean
     * @author eotouch
     * @date 2023-02-02 21:29
     */
    Boolean deleteBaseInfo(Long courseId);
}
