package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.model.po.CoursePublish;

import java.io.File;

/**
 * @author eotouch
 * @version 1.0
 * @description TODO
 * @date 2023/03/07 22:48
 */
public interface CoursePublishService {

    /**
     * @description 获取课程预览信息
     * @param courseId 课程id
     * @return com.xuecheng.content.model.dto.CoursePreviewDto
     * @author eotouch
     * @date 2023-03-07 22:49
     */
    CoursePreviewDto getCoursePreviewInfo(Long courseId);

    void uploadCourseHtml(Long courseId, File file);

    /**
     * @description 课程发布接口
     * @param companyId 机构id
     * @param courseId 课程id
     * @return void
     * @author eotouch
     * @date 2023-03-08 20:14
     */
    void publish(Long companyId,Long courseId);

    /**
     * @description 提交申请
     * @param companyId 机构id
     * @param courseId 课程id
     * @return void
     * @author eotouch
     * @date 2023-03-08 20:17
     */
    void commitAudit(Long companyId,Long courseId);

    File generateCourseHtml(Long courseId);

    Boolean saveCourseIndex(Long courseId);

    public CoursePublish getCoursePublish(Long courseId);
}
