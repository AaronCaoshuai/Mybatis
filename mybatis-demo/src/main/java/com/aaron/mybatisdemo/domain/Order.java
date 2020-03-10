package com.aaron.mybatisdemo.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 订单domain类
 */
public class Order implements Serializable {
    //主键
    private Integer id;
    //订单备注
    private String remark;
    //创建时间
    private Date createTime;
    //修改时间
    private Date updateTime;
    //用户id
    private Integer userId;
    //用户
    private User user = new User();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", remark='" + remark + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", userId=" + userId +
                ", user=" + user +
                '}';
    }
}
