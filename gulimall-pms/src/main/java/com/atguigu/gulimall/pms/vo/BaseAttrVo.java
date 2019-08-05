package com.atguigu.gulimall.pms.vo;

import lombok.Data;

import java.util.List;

@Data
public class BaseAttrVo {
    private Long attrId;
    private String attrName;
    private String[] valueSelected;
}


