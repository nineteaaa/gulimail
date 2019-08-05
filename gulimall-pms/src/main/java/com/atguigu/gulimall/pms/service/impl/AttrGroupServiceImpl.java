package com.atguigu.gulimall.pms.service.impl;

import com.atguigu.gulimall.pms.dao.AttrAttrgroupRelationDao;
import com.atguigu.gulimall.pms.dao.AttrDao;
import com.atguigu.gulimall.pms.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gulimall.pms.entity.AttrEntity;
import com.atguigu.gulimall.pms.vo.AttrGroupWithAttrsVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.Query;
import com.atguigu.gulimall.commons.bean.QueryCondition;

import com.atguigu.gulimall.pms.dao.AttrGroupDao;
import com.atguigu.gulimall.pms.entity.AttrGroupEntity;
import com.atguigu.gulimall.pms.service.AttrGroupService;

import javax.management.relation.Relation;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {
    @Autowired
    AttrAttrgroupRelationDao RelationDao;

    @Autowired
    AttrDao attrDao;

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public PageVo queryPageAttrGroupsByCatId(QueryCondition queryCondition, Long catId) {

        //构造查询条件
        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<AttrGroupEntity>()
                .eq("catelog_id ",catId);
        //获取分页条件
        IPage<AttrGroupEntity> page = new Query<AttrGroupEntity>().getPage(queryCondition);

        IPage<AttrGroupEntity> data = this.page(page, wrapper);
        //查出每一个分组的属性信息
        List<AttrGroupEntity> records = data.getRecords();
        List<AttrGroupWithAttrsVo> groupWithAttrs = new ArrayList<AttrGroupWithAttrsVo>(records.size());
            //获取分组的id
        for (AttrGroupEntity record : records) {
            AttrGroupWithAttrsVo vo = new AttrGroupWithAttrsVo();
            BeanUtils.copyProperties(records,vo);
            Long groupId = record.getAttrGroupId();
            List<AttrAttrgroupRelationEntity> relationEntities = RelationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", groupId));
           if (relationEntities!=null&&relationEntities.size()>0){
               ArrayList<Long> attrIds = new ArrayList<>();
               for (AttrAttrgroupRelationEntity relationEntity : relationEntities) {
                   Long attrId = relationEntity.getAttrId();
                   attrIds.add(attrId);
               }
               List<AttrEntity> attrEntities = attrDao.selectList(new QueryWrapper<AttrEntity>().in("attr_id", attrIds));
               vo.setAttrEntities(attrEntities);
           }
            groupWithAttrs.add(vo);
        }

    //	public PageVo(List<?> list, int totalCount, int pageSize, int currPage) {
        return new PageVo(groupWithAttrs,data.getTotal(),data.getSize(),data.getCurrent());

    }

}