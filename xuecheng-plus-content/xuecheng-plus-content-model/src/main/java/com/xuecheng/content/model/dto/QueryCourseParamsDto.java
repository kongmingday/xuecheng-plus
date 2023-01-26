package com.xuecheng.content.model.dto;

import lombok.Data;
import lombok.ToString;

/**
 * @author eotouch
 * @date 2023/01/21
 */
@Data
@ToString
public class QueryCourseParamsDto {

    //审核状态
    private String auditStatus;
    //课程名称
    private String courseName;
    //发布状态
    private String publishStatus;

}
