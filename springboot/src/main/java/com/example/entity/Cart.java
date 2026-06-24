package com.example.entity;

import java.io.Serializable;

/**
 * 购物车信息表
*/
public class Cart implements Serializable {
    private static final long serialVersionUID = 1L;

    /** ID */
    private Integer id;
    /** 用户ID */
    private Integer userId;
    /** 商品ID */
    private Integer goodsId;
    /** 商家ID */
    private Integer businessId;
    /** 商品数量 */
    private Integer num;


    /** 商品名称 */
    private String goodsName;
    /** 商家名称 */
    private String businessName;    
    /** 商品图片 */
    private String goodsImg;    
    /** 商品价格 */
    private Double goodsPrice;
    /** 计件单位 */
    private String goodsUnit;
        
    


    public Integer getId() {
        return id;
    }  

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getBusinessId() {
        return businessId;
    }
    
    public void setBusinessId(Integer businessId) {
        this.businessId = businessId;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getGoodsImg() {
        return goodsImg;
    }

    public void setGoodsImg(String goodsImg) {
        this.goodsImg = goodsImg;
    }

    public Double getGoodsPrice() {
        return goodsPrice;
    }

    public void setGoodsPrice(Double goodsPrice) {
        this.goodsPrice = goodsPrice;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }
    public String getGoodsUnit() {
        return goodsUnit;
    }

    public void setGoodsUnit(String goodsUnit) {
        this.goodsUnit = goodsUnit;

    }
}
