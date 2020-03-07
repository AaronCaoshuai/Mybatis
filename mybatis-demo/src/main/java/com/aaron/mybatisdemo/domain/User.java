package com.aaron.mybatisdemo.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 用户domain类
 */
public class User {
    //用户ID
    private Integer id;
    //姓名
    private String username;
    //生日
    private Date birthday;
    //性别(可以用枚举 int类型代替)
    private String sex;
    //地址
    private String address;
    //订单集合
    private List<Order> orders = new ArrayList<Order>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", birthday=" + birthday +
                ", sex='" + sex + '\'' +
                ", address='" + address + '\'' +
                ", orders=" + orders +
                '}';
    }
}
