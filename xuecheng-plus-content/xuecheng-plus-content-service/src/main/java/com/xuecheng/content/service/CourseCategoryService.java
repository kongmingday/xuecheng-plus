package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description TODO
 * @author eotouch
 * @version 1.0
 * @date 2023/01/26 16:58
 */
public interface CourseCategoryService {

    List<CourseCategoryTreeDto> queryTreeNodes(String id);
}
