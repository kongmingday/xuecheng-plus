package com.xuecheng.content.service;

import com.xuecheng.base.model.RestResponse;
import com.xuecheng.content.model.dto.BindTeachplanMediaDto;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.TeachplanMedia;

import java.util.List;

/**
 * @author eotouch
 * @version 1.0
 * @description 课程基本信息管理业务接口
 * @date 2023/02/28 23:20
 */
public interface TeachplanService {

    /**
     * @description 查询课程计划树形结构
     * @param courseId 课程id
     * @return java.util.List<com.xuecheng.content.model.dto.TeachplanDto>
     * @author eotouch
     * @date 2023-02-28 23:21
     */
     List<TeachplanDto> findTeachplanTree(Long courseId);

    /**
     * @description 增加课程计划
     * @param teachplanDto 课程计划id
     * @return void
     * @author eotouch
     * @date 2023-03-01 8:41
     */
     void saveTeachplan(SaveTeachplanDto teachplanDto);


     /**
      * @description 删除课程计划
      * @param id 课程计划id
      * @return void
      * @author eotouch
      * @date 2023-03-04 11:24
      */
    void removeTeaplan(Long id);

    /**
     * @description 课程计划上移
     * @param id 课程计划id
     * @return void
     * @author eotouch
     * @date 2023-03-04 11:42
     */
    void moveUpTeachlan(Long id);

    /**
     * @description 课程计划下移
     * @param id 课程计划id
     * @return void
     * @author eotouch
     * @date 2023-03-04 11:42
     */
    void moveDownTeachlan(Long id);

    /**
     * @description 课程计划和媒资信息绑定
     * @param bindTeachplanMediaDto 绑定教学计划及媒资参数
     * @return com.xuecheng.content.model.po.TeachplanMedia
     * @author eotouch
     * @date 2023-03-05 21:05
     */
    TeachplanMedia associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto);

    /**
     * @description 课程计划和媒资信息解绑
     * @param teachPlanId 教学计划id
     * @param mediaId 媒资信息id
     * @return com.xuecheng.base.model.RestResponse
     * @author eotouch
     * @date 2023-03-05 21:35
     */
    RestResponse disassociationMedia(Long teachPlanId, String mediaId);
}
