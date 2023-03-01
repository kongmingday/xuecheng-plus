package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;

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
    public List<TeachplanDto> findTeachplanTree(Long courseId);

    /**
     * @description 增加课程计划
     * @param teachplanDto 课程计划id
     * @return void
     * @author eotouch
     * @date 2023-03-01 8:41
     */
    public void saveTeachplan(SaveTeachplanDto teachplanDto);
}
