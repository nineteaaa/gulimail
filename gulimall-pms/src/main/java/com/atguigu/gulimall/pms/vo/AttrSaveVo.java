package com.atguigu.gulimall.pms.vo;

import com.atguigu.gulimall.pms.entity.AttrEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AttrSaveVo extends AttrEntity {
    @ApiModelProperty(name = "groupId",value = "分组id")
    private Long groupId;
}
