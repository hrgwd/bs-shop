package com.example.entity;

import java.io.Serializable;

/**
 * 评论信息表
*/
public class Comment implements Serializable {
    private static final long serialVersionUID = 1L;

    /** ID */
    private Integer id;
    /** 用户ID */
    private Integer userId;
    /** 商品ID */
    private Integer goodsId;
    /** 商家ID */
    private Integer businessId;
    /** 商品名称 */
    private String goodsName;
    /** 商家名称 */
    private String businessName;
    /** 评论内容 */
    private String content; 
    /** 评论时间 */
    private String time;
    /** 用户头像 */
    private  String userAvatar;
    /** 用户名称 */
    private  String userName;
    


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

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }
    
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getUserName() {
        return userName;
    }
}
