package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.mapper.TeachplanMediaMapper;
import com.xuecheng.content.model.dto.BindTeachplanMediaDto;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import com.xuecheng.content.service.TeachplanService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author eotouch
 * @version 1.0
 * @description TODO
 * @date 2023/02/28 23:22
 */
@Service
public class TeachplanServiceImpl implements TeachplanService {

    @Autowired
    private TeachplanMapper teachplanMapper;

    @Autowired
    private TeachplanMediaMapper teachplanMediaMapper;

    @Override
    public List<TeachplanDto> findTeachplanTree(Long courseId) {
        return teachplanMapper.selectTreeNodes(courseId);
    }

    @Override
    public void saveTeachplan(SaveTeachplanDto teachplanDto) {
        //课程计划id
        Long id = teachplanDto.getId();
        //修改课程计划
        if(id!=null){
            Teachplan teachplan = teachplanMapper.selectById(id);
            BeanUtils.copyProperties(teachplanDto,teachplan);
            teachplanMapper.updateById(teachplan);
        }else{
            //取出同父同级别的课程计划数量
            int count = getTeachplanOrderMax(teachplanDto.getCourseId(), teachplanDto.getParentid());

            Teachplan teachplanNew = new Teachplan();
            //设置排序号
            teachplanNew.setOrderby(count+1);
            BeanUtils.copyProperties(teachplanDto,teachplanNew);

            teachplanMapper.insert(teachplanNew);

        }
    }

    @Override
    public void removeTeaplan(Long id) {
        Teachplan teachplan = teachplanMapper.selectById(id);
        if (teachplan == null){
            XueChengPlusException.cast("该课程计划已被删除或不存在");
        }
        Long parentid = teachplan.getParentid();
        if (parentid != 0){
            teachplanMapper.deleteById(id);
            return;
        }
        LambdaQueryWrapper<Teachplan> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Teachplan::getParentid, teachplan.getId());
        List<Teachplan> teachplans = teachplanMapper.selectList(lqw);
        if (teachplans != null){
            XueChengPlusException.cast("该课程计划下还有其他小节，请勿删除");
        }
        teachplanMapper.deleteById(id);
    }

    @Override
    public void moveUpTeachlan(Long id) {
        Teachplan teachplan = teachplanMapper.selectById(id);
        Teachplan exchange = selectClosedTeachplan(teachplan, id, true);
        exchangeOrderBy(teachplan, exchange);
    }

    @Override
    public void moveDownTeachlan(Long id) {
        Teachplan teachplan = teachplanMapper.selectById(id);
        Teachplan exchange = selectClosedTeachplan(teachplan, id, false);
        exchangeOrderBy(teachplan, exchange);
    }

    @Override
    public TeachplanMedia associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto) {
        Long teachplanId = bindTeachplanMediaDto.getTeachplanId();
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        if(teachplan==null){
            XueChengPlusException.cast("教学计划不存在");
        }
        Integer grade = teachplan.getGrade();
        if(grade!=2){
            XueChengPlusException.cast("只允许第二级教学计划绑定媒资文件");
        }
        //课程id
        Long courseId = teachplan.getCourseId();

        //先删除原来该教学计划绑定的媒资
        teachplanMediaMapper.delete(new LambdaQueryWrapper<TeachplanMedia>().eq(TeachplanMedia::getTeachplanId,teachplanId));

        //再添加教学计划与媒资的绑定关系
        TeachplanMedia teachplanMedia = new TeachplanMedia();
        teachplanMedia.setCourseId(courseId);
        teachplanMedia.setTeachplanId(teachplanId);
        teachplanMedia.setMediaFilename(bindTeachplanMediaDto.getFileName());
        teachplanMedia.setMediaId(bindTeachplanMediaDto.getMediaId());
        teachplanMedia.setCreateDate(LocalDateTime.now());
        teachplanMediaMapper.insert(teachplanMedia);
        return teachplanMedia;
    }

    @Override
    public RestResponse disassociationMedia(Long teachPlanId, String mediaId) {
        Teachplan teachplan = teachplanMapper.selectById(teachPlanId);
        if (teachplan == null){
            XueChengPlusException.cast("该教学计划已不存在");
        }
        LambdaQueryWrapper<TeachplanMedia> lqw = new LambdaQueryWrapper<>();
        lqw.eq(TeachplanMedia::getTeachplanId, teachPlanId).eq(TeachplanMedia::getMediaId, mediaId);
        int count = teachplanMediaMapper.delete(lqw);
        return count > 0 ? RestResponse.success(200) : RestResponse.validfail("删除教学失败");
    }

    /**
     * @description 寻找最靠近的课程计划
     * @param id 课程计划id
     * @param isUp 是否为上移
     * @param teachplan 目标课程计划
     * @return com.xuecheng.content.model.po.Teachplan
     * @author eotouch
     * @date 2023-03-04 12:01
     */
    private Teachplan selectClosedTeachplan(Teachplan teachplan, Long id, boolean isUp){
        LambdaQueryWrapper<Teachplan> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Teachplan::getParentid, teachplan.getParentid());
        lqw.eq(Teachplan::getCourseId, teachplan.getCourseId());
        lqw.eq(Teachplan::getGrade, teachplan.getGrade());
        if (isUp){
            lqw.lt(Teachplan::getOrderby, teachplan.getOrderby());
            lqw.orderByAsc(Teachplan::getOrderby);
        }else {
            lqw.gt(Teachplan::getOrderby, teachplan.getOrderby());
            lqw.orderByDesc(Teachplan::getOrderby);
        }
        List<Teachplan> teachplans = teachplanMapper.selectList(lqw);
        if (teachplans == null || teachplans.size() == 0){
            XueChengPlusException.cast("该章节已至最顶或最低, 无法移动");
        }

        return teachplans.get(teachplans.size()-1);
    }

    private void exchangeOrderBy(Teachplan target, Teachplan exchange){
        Integer targetOrderby = target.getOrderby();
        target.setOrderby(exchange.getOrderby());
        exchange.setOrderby(targetOrderby);
        teachplanMapper.updateById(target);
        teachplanMapper.updateById(exchange);
    }

    /**
     * @description 获取最新排序号
     * @param courseId 课程id
     * @param parentId 父章节id
     * @return int 最新排序好
     * @author eotouch
     * @date 2023-03-01 19:53
     */
    private int getTeachplanOrderMax(long courseId,long parentId){
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getCourseId,courseId);
        queryWrapper.eq(Teachplan::getParentid,parentId);
        queryWrapper.orderByDesc(Teachplan::getOrderby);
        List<Teachplan> teachplans = teachplanMapper.selectList(queryWrapper);
        if (teachplans.size() == 0){
            return 1;
        }
        return teachplans.get(0).getOrderby();
    }
}
