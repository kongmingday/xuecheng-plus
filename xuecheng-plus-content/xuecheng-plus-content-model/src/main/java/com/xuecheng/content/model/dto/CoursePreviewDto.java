package com.xuecheng.content.model.dto;

import lombok.Data;

import java.util.List;

/**
 * @author eotouch
 * @version 1.0
 * @description TODO
 * @date 2023/03/07 22:48
 */
@Data
public class CoursePreviewDto {
    //课程基本信息,课程营销信息
    CourseBaseInfoDto courseBase;


    //课程计划信息
    List<TeachplanDto> teachplans;

}
