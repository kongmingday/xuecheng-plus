package com.xuecheng.content.service.impl;/**
 * @description TODO
 * @author eotouch
 * @date 2023/01/22  21:08
 * @version 1.0
 */

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.service.CourseBaseInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @description TODO
 * @author eotouch
 * @date 2023/01/22 21:08
 * @version 1.0
 */
@Service
public class CourseBaseInfoServiceImpl extends ServiceImpl<CourseBaseMapper, CourseBase> implements CourseBaseInfoService {

    @Autowired
    private CourseBaseMapper courseBaseMapper;

    @Override
    public PageResult<CourseBase> queryCourseBaseList(PageParams params, QueryCourseParamsDto queryCourseParamsDto) {

        // 查询条件构造
        LambdaQueryWrapper<CourseBase> lqw = new LambdaQueryWrapper<>();

        // 模糊查询课程名 + 精确查询课程审核状态
        lqw.like(StringUtils.isNotEmpty(queryCourseParamsDto.getCourseName()), CourseBase::getName, queryCourseParamsDto.getCourseName());
        lqw.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getAuditStatus()), CourseBase::getAuditStatus, queryCourseParamsDto.getAuditStatus());

        // 精确查询课程发布状态

        // 分页条件构造
        Page<CourseBase> page = new Page<>(params.getPageNo(), params.getPageSize());

        // 查询
        Page<CourseBase> pageResult = courseBaseMapper.selectPage(page, lqw);

        // 返回参数构造
        List<CourseBase> item = pageResult.getRecords();
        long total = pageResult.getTotal();

        PageResult<CourseBase> courseBasePageResult = new PageResult<CourseBase>(item, total, params.getPageNo(), params.getPageSize());

        return courseBasePageResult;
    }

}
