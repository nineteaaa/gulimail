package com.atguigu.gulimall.pms.vo;

import com.atguigu.gulimall.pms.entity.SkuInfoEntity;
import com.atguigu.gulimall.pms.entity.SpuInfoEntity;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SpuAllSaveVo  extends SpuInfoEntity {

    private String[] SpuImages;

    private List<BaseAttrVo> baseAttrsVos;

    private List<SkuVo> skus;



}





