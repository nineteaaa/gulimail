package com.atguigu.gulimall.pms.vo;


import com.atguigu.gulimall.pms.entity.SkuInfoEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
@Data
public class SkuVo {

    /**
     *
     "skuName": "", //sku名字
     "skuDesc": "", //sku描述
     "skuTitle" : "", //sku标题
     "skuSubtitle" :"", //sku副标题
     "weight" : 0, //sku重量
     "price": 0, //商品价格
     "images": ["string"], //sku图片
     */
    private String skuName;
    private String skuDesc;
    private String skuTitle;
    private String skuSubstitle;
    private BigDecimal weight;
    private BigDecimal price;
    private String[] images;
    //以上sku的基本信息




    /*
     "saleAttrs": [ //销售属性组合
     {
     "attrId": 0, //属性id
     "attrValue": "string" //属性值
     }],
     */
    private List<SaleAttrVo> saleAttrVos;




    /*

     "buyBounds": 0, //赠送的购物积分
     "growBounds": 0, //赠送的成长积分
     "work": [0,1,1,0], //积分生效情况
     */
    private BigDecimal buyBounds;
    private BigDecimal growBounds;
    //优惠生效情况[1111（四个状态位，从右到左）;
    // 0 - 无优惠，成长积分是否赠送;
    // 1 - 无优惠，购物积分是否赠送;
    // 2 - 有优惠，成长积分是否赠送;
    // 3 - 有优惠，购物积分是否赠送【状态位0：不赠送，1：赠送】]
    private Integer[] work;
    //上面是积分设置的信息


     /*
     "fullCount": 0, //满几件
     "discount": 0, //打几折
     "ladderAddOther": 0, //阶梯价格是否可以与其他优惠叠加
     */
     private Integer fullcount;
     private BigDecimal discount;
     private Integer ladderAddOther;

      /*
     "fullPrice": 0, //满多少
     "reducePrice": 0, //减多少
     "fullAddOther": 0, //满减是否可以叠加其他优惠
     }
     ]
     */
      private BigDecimal fullPrice;
      private BigDecimal reducePrice;
      private Integer  fullAddOther;

}
