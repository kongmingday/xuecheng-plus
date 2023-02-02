package com.xuecheng.content.service.impl;

import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.model.po.CourseCategory;
import com.xuecheng.content.service.CourseCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @description TODO
 * @author eotouch
 * @version 1.0
 * @date 2023/01/26 16:58
 */
@Service
public class CourseCategoryServiceImpl implements CourseCategoryService {

    @Autowired
    private CourseCategoryMapper courseCategoryMapper;

    @Override
    public List<CourseCategoryTreeDto> queryTreeNodes(String id) {

        List<CourseCategoryTreeDto> categoryTreeDtos = courseCategoryMapper.selectTreeNodes(id);

        List<CourseCategoryTreeDto> courseCategoryTreeDtos = new ArrayList<>();

        HashMap<String, CourseCategoryTreeDto> nodeMap = new HashMap<>();

        categoryTreeDtos.stream().forEach(item -> {
            nodeMap.put(item.getId(), item);
            if(item.getParentid().equals(id)){
                courseCategoryTreeDtos.add(item);
            }
            String parentId = item.getParentid();
            CourseCategoryTreeDto parentNode = nodeMap.get(parentId);
            if (parentNode != null){
                List childrenNodes = parentNode.getChildrenTreeNodes();
                if (childrenNodes == null){
                    parentNode.setChildrenTreeNodes(new ArrayList<CourseCategoryTreeDto>());
                }
                parentNode.getChildrenTreeNodes().add(item);
            }
        });

        return courseCategoryTreeDtos;
    }
}
