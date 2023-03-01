package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author eotouch
 * @version 1.0
 * @description TODO
 * @date 2023/02/28 22:51
 */

@Data
@ToString
public class TeachplanDto extends Teachplan {

    List<TeachplanDto> teachplanTreeNodes;

    TeachplanMedia teachplanMedia;
}
