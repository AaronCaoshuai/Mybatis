package com.aaron.mybatisdemo.mapper;

import com.aaron.mybatisdemo.domain.User;
import com.aaron.mybatisdemo.queryvo.UserQueryVO;
import org.apache.ibatis.annotations.Param;

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
    User findUserById(Integer id) throws Exception;

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
    void deleteUserById(Integer id) throws Exception;

    /**
     * 根据查询对象查询用户
     * @param userQueryVO
     * @return
     * @throws Exception
     */
    List<User> queryUsersByCondition(@Param("userQueryVO") UserQueryVO userQueryVO) throws Exception;

    /**
     * 查询用户数量
     * @return
     * @throws Exception
     */
    int selectUserCount(UserQueryVO userQueryVO) throws Exception;

    /**
     * 根据用户id查询用户及其下所有的订单信息
     * @param userId
     * @return
     * @throws Exception
     */
    User selectOrdersByUserId(Integer userId) throws Exception;
}
