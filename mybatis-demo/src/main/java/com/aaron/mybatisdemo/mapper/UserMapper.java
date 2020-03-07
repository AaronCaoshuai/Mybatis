package com.aaron.mybatisdemo.mapper;

import com.aaron.mybatisdemo.domain.User;

import java.util.List;

/**
 * UserMapper对象 Mapper代理开发(xml)方式
 */
public interface UserMapper {

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

    /**
     * 删除用户
     * @param id
     * @throws Exception
     */
    void deleteUserById(int id) throws Exception;
}
