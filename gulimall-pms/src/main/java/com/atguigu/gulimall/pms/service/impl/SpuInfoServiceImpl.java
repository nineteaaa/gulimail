package com.atguigu.gulimall.pms.service.impl;

import com.atguigu.gulimall.commons.utils.AppUtils;
import com.atguigu.gulimall.pms.dao.*;
import com.atguigu.gulimall.pms.entity.*;
import com.atguigu.gulimall.commons.to.SkuSaleInfoTo;
import com.atguigu.gulimall.pms.feign.SmsSkuInfoFeignService;
import com.atguigu.gulimall.pms.vo.BaseAttrVo;
import com.atguigu.gulimall.pms.vo.SaleAttrVo;
import com.atguigu.gulimall.pms.vo.SkuVo;
import com.atguigu.gulimall.pms.vo.SpuAllSaveVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.Query;
import com.atguigu.gulimall.commons.bean.QueryCondition;

import com.atguigu.gulimall.pms.service.SpuInfoService;
import org.springframework.util.StringUtils;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {
    @Autowired
    SpuInfoDao spuInfoDao;
    @Autowired
    SpuInfoDescDao spuInfoDescDao;
    @Autowired
    ProductAttrValueDao productAttrValueDao;
    @Autowired
    SkuInfoDao skuInfoDao;
    @Autowired
    SkuImagesDao skuImagesDao;
    @Autowired
    SkuSaleAttrValueDao skuSaleAttrValueDao;
    @Autowired
    AttrDao attrDao;
    @Autowired
    SmsSkuInfoFeignService smsSkuInfoFeignService;
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

    @Override
    public void spuBigSaveAll(SpuAllSaveVo spuInfo) {
        //1.保存spu 基本信息
        Long spuId = this.saveSpuBaseInfo(spuInfo);
        //1.2保存spu的所有图片信息
        this.saveSpuImagesInfo(spuInfo.getSpuImages(),spuId);
        //2.保存spu基本属性信息
        this.saveSpuBaseAttr(spuInfo.getBaseAttrsVos(),spuId);
        BaseAttrVo baseAttrVo = new BaseAttrVo();
        BeanUtils.copyProperties(spuInfo,baseAttrVo);
        //3.保存sku信息及营销信息
        this.saveSkuInfos(spuId,spuInfo.getSkus());
    }

    @Override
    public Long saveSpuBaseInfo(SpuAllSaveVo spuInfo) {
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(spuInfo,spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUodateTime(new Date());
        spuInfoDao.insert(spuInfoEntity);
        return spuInfoEntity.getId();
    }

    @Override
    public void saveSpuImagesInfo(String[] spuImages, Long spuId) {
        StringBuffer urls = new StringBuffer();
        for (String image : spuImages) {
            urls.append(image).append(",");
        }
        urls.substring(0,urls.length()-1);
        SpuInfoDescEntity descEntity = new SpuInfoDescEntity();
        descEntity.setSpuId(spuId);
        descEntity.setDecript(urls.toString());
        spuInfoDescDao.insert(descEntity);
    }

    @Override
    public void saveSpuBaseAttr(List<BaseAttrVo> baseAttrsVos, Long spuId) {
        List<ProductAttrValueEntity> allSave = new ArrayList<>();
        for (BaseAttrVo baseAttrsVo : baseAttrsVos) {
            ProductAttrValueEntity entity = new ProductAttrValueEntity();
            entity.setSpuId(spuId);
            entity.setAttrId(baseAttrsVo.getAttrId());
            entity.setAttrName(baseAttrsVo.getAttrName());
            String[] valueSelected = baseAttrsVo.getValueSelected();
            entity.setAttrValue(AppUtils.arrayToStringWithSeparator(valueSelected,","));
            entity.setAttrSort(0);
            entity.setQuickShow(1);
        }
        productAttrValueDao.insertBatch(allSave);

    }

    @Override
    public void saveSkuInfos(Long spuId, List<SkuVo> skus) {
        //查出sku中的冗余字段
            //获取spu信息
        SpuInfoEntity spuInfo = this.getById(spuId);
        ArrayList<SkuSaleInfoTo> tos = new ArrayList<>();
        //保存sku信息
        for (SkuVo skuVo : skus) {
            String[] images = skuVo.getImages();
            SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
            skuInfoEntity.setBrandId(spuInfo.getBrandId());
            skuInfoEntity.setCatalogId(spuInfo.getCatalogId());
            skuInfoEntity.setPrice(skuVo.getPrice());
            skuInfoEntity.setSkuCode(UUID.randomUUID().toString().substring(0,5).toUpperCase());
            if (images!=null&&images.length>0){
                skuInfoEntity.setSkuDefaultImg(skuVo.getImages()[0]);
            }
            skuInfoEntity.setSkuDesc(skuVo.getSkuDesc());
            skuInfoEntity.setSkuName(skuVo.getSkuName());
            skuInfoEntity.setSkuTitle(skuVo.getSkuTitle());
            skuInfoEntity.setSkuSubtitle(skuVo.getSkuSubstitle());
            skuInfoEntity.setWeight(skuVo.getWeight());
        //保存sku的基本信息
            skuInfoDao.insert(skuInfoEntity);
            Long skuId = skuInfoEntity.getSkuId();
            //保存sku所有的对应图片
//            for (String image : images) {
//                SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
//                skuImagesEntity.setImgUrl(image);
//                skuImagesEntity.setSkuId(skuId);
//                skuImagesDao.insert(skuImagesEntity);
//            }
            for (int i = 0; i < images.length; i++) {
                SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                skuImagesEntity.setSkuId(skuId);
                skuImagesEntity.setDefaultImg(i==0?1:0);
                skuImagesEntity.setImgUrl(images[i]);
                skuImagesEntity.setImgSort(0);
                skuImagesDao.insert(skuImagesEntity);
            }
            //保存销售属性组合

            List<SaleAttrVo> saleAttrs = skuVo.getSaleAttrVos();
            for (SaleAttrVo saleAttr : saleAttrs) {
                SkuSaleAttrValueEntity entity = new SkuSaleAttrValueEntity();
                entity.setAttrId(saleAttr.getAttrId());
                entity.setAttrValue(saleAttr.getAttrValue());
                AttrEntity attrEntity = attrDao.selectById(saleAttr.getAttrId());
                entity.setAttrName(attrEntity.getAttrName());
                entity.setAttrSort(0);
                entity.setSkuId(skuId);
                skuSaleAttrValueDao.insert(entity);
            }

            //以上都是pms系统完成的工作

            //以下需要由sms完成,保存每一个sku的相关优惠信息数据
            SkuSaleInfoTo skuSaleInfoTo = new SkuSaleInfoTo();
            BeanUtils.copyProperties(skuVo,skuSaleInfoTo);
            skuSaleInfoTo.setSkuId(skuId);
            tos.add(skuSaleInfoTo);
            //发给sms,让他处理,这里不管
        }

        smsSkuInfoFeignService.saveSkuSaleInfos(tos);
    }


}