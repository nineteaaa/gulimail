package com.atguigu.gulimall.pms.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.Query;
import com.atguigu.gulimall.commons.bean.QueryCondition;

import com.atguigu.gulimall.pms.dao.SpuInfoDao;
import com.atguigu.gulimall.pms.entity.SpuInfoEntity;
import com.atguigu.gulimall.pms.service.SpuInfoService;
import org.springframework.util.StringUtils;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public PageVo queryPageByCatId(Long catId, QueryCondition queryCondition) {
        //查所有
        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();
        if (catId!=0) {
            wrapper.eq("catalog_id",catId);
            if(!StringUtils.isEmpty(queryCondition.getKey())){
                wrapper.and(obj->{
                   obj.like("spu_name",queryCondition.getKey());
                   obj.or().like("id",queryCondition.getKey());
                   return obj;
                });
            }
        }

        //封装翻页条件
        IPage<SpuInfoEntity> page = new Query<SpuInfoEntity>().getPage(queryCondition);
        //取数据库查询
        IPage<SpuInfoEntity> data = this.page(page, wrapper);

        PageVo vo = new PageVo(data);

        return vo;
    }

}