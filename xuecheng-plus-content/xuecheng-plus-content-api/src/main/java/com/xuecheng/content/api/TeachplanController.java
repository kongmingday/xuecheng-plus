package com.xuecheng.content.api;

import com.xuecheng.base.model.RestResponse;
import com.xuecheng.content.model.dto.BindTeachplanMediaDto;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.service.TeachplanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author eotouch
 * @version 1.0
 * @description 课程计划控制层
 * @date 2023/02/28 22:53
 */

@RestController
@Api(value = "课程计划管理相关接口", tags = "课程管理相关接口")
@RequestMapping("/teachplan")
public class TeachplanController {

    @Autowired
    private TeachplanService teachplanService;

    @ApiOperation("查询课程计划树形结构")
    @GetMapping("/{courseId}/tree-nodes")
    public List<TeachplanDto> getTreeNodes(@PathVariable Long courseId){
        List<TeachplanDto> l = teachplanService.findTeachplanTree(courseId);
        return teachplanService.findTeachplanTree(courseId);
    }

    @ApiOperation("课程计划创建或修改")
    @PostMapping
    public void saveTeachplan(@RequestBody SaveTeachplanDto teachplan){
        teachplanService.saveTeachplan(teachplan);
    }

    @ApiOperation("课程计划删除")
    @DeleteMapping("/{id}")
    public void removeTeachplan(@PathVariable Long id){
        teachplanService.removeTeaplan(id);
    }

    @ApiOperation("课程计划上移")
    @PostMapping("/moveup/{id}")
    public void moveUpTeachplan(@PathVariable Long id) { teachplanService.moveUpTeachlan(id); }

    @ApiOperation("课程计划下移")
    @PostMapping("/movedown/{id}")
    public void moveDownTeachplan(@PathVariable Long id) { teachplanService.moveDownTeachlan(id); }

    @ApiOperation("课程计划和媒资信息绑定")
    @PostMapping("/association/media")
    public void associationMedia(@RequestBody BindTeachplanMediaDto bindTeachplanMediaDto){
        teachplanService.associationMedia(bindTeachplanMediaDto);
    }

    @ApiOperation("课程计划和媒资信息解绑")
    @DeleteMapping("/association/media/{teachPlanId}/{mediaId}")
    public RestResponse disassociationMedia(@PathVariable Long teachPlanId, @PathVariable String mediaId){
        return teachplanService.disassociationMedia(teachPlanId, mediaId);
    }
}
