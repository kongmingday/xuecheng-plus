package com.xuecheng.content.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author eotouch
 * @version 1.0
 * @description TODO
 * @date 2023/02/01 22:31
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value="EditCourseDto", description="修改课程基本信息")
public class EditCourseDto extends AddCourseDto {

    @ApiModelProperty(value = "课程名称", required = true)
    private Long id;

}
