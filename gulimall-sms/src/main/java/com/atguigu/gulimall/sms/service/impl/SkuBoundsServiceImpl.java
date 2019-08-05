package com.atguigu.gulimall.sms.service.impl;

import com.atguigu.gulimall.commons.to.SkuSaleInfoTo;
import com.atguigu.gulimall.sms.dao.SkuFullReductionDao;
import com.atguigu.gulimall.sms.dao.SkuLadderDao;
import com.atguigu.gulimall.sms.entity.SkuFullReductionEntity;
import com.atguigu.gulimall.sms.entity.SkuLadderEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.parsing.BeanEntry;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.Query;
import com.atguigu.gulimall.commons.bean.QueryCondition;

import com.atguigu.gulimall.sms.dao.SkuBoundsDao;
import com.atguigu.gulimall.sms.entity.SkuBoundsEntity;
import com.atguigu.gulimall.sms.service.SkuBoundsService;


@Service("skuBoundsService")
public class SkuBoundsServiceImpl extends ServiceImpl<SkuBoundsDao, SkuBoundsEntity> implements SkuBoundsService {

    @Autowired
    SkuBoundsDao skuBoundsDao;
    @Autowired
    SkuFullReductionDao skuFullReductionDao;
    @Autowired
    SkuLadderDao skuLadderDao;

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<SkuBoundsEntity> page = this.page(
                new Query<SkuBoundsEntity>().getPage(params),
                new QueryWrapper<SkuBoundsEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public void saveSkuAllSaleInfo(List<SkuSaleInfoTo> to) {
        if (to !=null&&to.size()>0){
            for (SkuSaleInfoTo info : to) {
                //积分信息的保存
                SkuBoundsEntity skuBoundsEntity = new SkuBoundsEntity();
                Integer[] work = info.getWork();
                Integer i = work[3] * 1 + work[2] * 2 + work[1] * 4 + work[0] * 8;
                skuBoundsEntity.setWork(i);
                skuBoundsEntity.setBuyBounds(info.getBuyBounds());
                skuBoundsEntity.setGrowBounds(info.getGrowBounds());
                skuBoundsEntity.setSkuId(info.getSkuId());
                skuBoundsDao.insert(skuBoundsEntity);
                //阶梯价格的保存
                SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
                skuLadderEntity.setAddOther(info.getLadderAddOther());
                skuLadderEntity.setDiscount(info.getDiscount());
                skuLadderEntity.setFullCount(info.getFullcount());
                skuBoundsEntity.setSkuId(info.getSkuId());
                skuLadderDao.insert(skuLadderEntity);
                //满减信息的保存
                SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
                BeanUtils.copyProperties(info,skuFullReductionEntity);
               // skuFullReductionEntity.setFullPrice(info.getFullPrice());
                skuFullReductionEntity.setAddOther(info.getFullAddOther());
                //skuFullReductionEntity.setSkuId(info.getSkuId());
                skuFullReductionDao.insert(skuFullReductionEntity);

            }
        }
    }

}