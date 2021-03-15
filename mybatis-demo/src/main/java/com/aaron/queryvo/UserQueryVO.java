package com.aaron.queryvo;

/**
 * 用户条件查询对象
 */
public class UserQueryVO {
    //地址
    private String address;
    //性别
    private String sex;

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

    @Override
    public String toString() {
        return "UserQueryVO{" +
                "address='" + address + '\'' +
                ", sex='" + sex + '\'' +
                '}';
    }
}
