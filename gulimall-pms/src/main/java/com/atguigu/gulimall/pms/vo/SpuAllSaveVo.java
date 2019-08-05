package com.atguigu.gulimall.pms.vo;

import com.atguigu.gulimall.pms.entity.SkuInfoEntity;
import lombok.Data;

import java.util.List;

@Data
public class SpuAllSaveVo extends SkuInfoEntity {

    private String[] SpuImages;

    private List<BaseAttrVo> baseAttrsVos;

    private List<SkuVo> skus;

}





