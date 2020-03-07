package com.aaron.mybatisdemo.dao;

import com.aaron.mybatisdemo.domain.User;

import java.util.List;

/**
 * 用户dao类 mapper基础开发方式
 */
public interface UserDao {
    /**
     * 根据用户ID查询用户信息
     * @param id
     * @throws Exception
     * @return
     */
    User findUserById(int id) throws Exception;

    /**
     * 根据用户姓名查找用户列表
     * @param name
     * @throws Exception
     * @return
     */
    List<User> findUserByName(String name) throws Exception;

    /**
     * 添加用户
     * @param user
     * @throws Exception
     */
    void insertUser(User user) throws Exception;
}
