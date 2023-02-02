package com.xuecheng.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.model.po.CourseCategory;

import java.util.List;

/**
 * <p>
 * 课程分类 Mapper 接口
 * </p>
 *
 * @author itcast
 */
public interface CourseCategoryMapper extends BaseMapper<CourseCategory> {

    /**
     * @description 查询课程分类树
     * @param id 查询树的父级节点id
     * @return java.util.List<com.xuecheng.content.model.dto.CourseCategoryTreeDto>
     * @author eotouch
     * @date 2023-01-26 17:39
     */
    List<CourseCategoryTreeDto> selectTreeNodes(String id);

}
