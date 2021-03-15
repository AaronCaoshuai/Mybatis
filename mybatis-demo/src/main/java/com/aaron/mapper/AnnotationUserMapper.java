package com.aaron.mapper;

import com.aaron.domain.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * mapper代理(注解)开发
 */
public interface AnnotationUserMapper {


    /**
     * 根据用户ID查询用户信息
     * @param id
     * @throws Exception
     * @return
     */
    @Select("select * from user where id = #{id}")
    User findUserById(int id) throws Exception;

    /**
     * 根据用户姓名查找用户列表
     * @param name
     * @throws Exception
     * @return
     */
    @Select("select * from user where username like '%${value}%'")
    List<User> findUserByName(String name) throws Exception;

    /**
     * 添加用户
     * @param user
     * @throws Exception
     */
    @Insert("insert into user (username,birthday,sex,address) values(#{username},#{birthday},#{sex},#{address})")
    @SelectKey(statement = "select LAST_INSERT_ID()",keyProperty = "id",before = false,resultType = java.lang.Integer.class)
    void insertUser(User user) throws Exception;

    /**
     * 删除用户
     * @param id
     * @throws Exception
     */
    @Delete("delete from user where id = #{id}")
    void deleteUserById(int id) throws Exception;
}
