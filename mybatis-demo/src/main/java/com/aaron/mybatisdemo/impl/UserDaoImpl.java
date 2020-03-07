package com.aaron.mybatisdemo.impl;

import com.aaron.mybatisdemo.dao.UserDao;
import com.aaron.mybatisdemo.domain.User;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;

/**
 * 用户dao实现类
 */
public class UserDaoImpl implements UserDao {

    //全局范围（应用级别）
    private SqlSessionFactory sqlSessionFactory;

    //注入SqlSessionFactory
    public UserDaoImpl(SqlSessionFactory sqlSessionFactory){
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public User findUserById(int id) throws Exception {
        //方法级别
        SqlSession session = sqlSessionFactory.openSession();
        User user = null;
        try {
            //通过sqlsession调用selectOne方法获取一条结果集
            //参数1：指定定义的statement的id,参数2：指定向statement中传递的参数
            user = session.selectOne("user.findUserById", id);
            System.out.println(user);

        } finally{
            session.close();
        }
        return user;
    }

    public List<User> findUserByName(String name) throws Exception {
        SqlSession session = sqlSessionFactory.openSession();
        List<User> users = null;
        try{
            users = session.selectList("user.findUserByName",name);
            for(User user : users){
                System.out.println(user);
            }
            System.out.println(users);
        }finally{
            session.close();
        }
        return users;
    }

    public void insertUser(User user) throws Exception {
        SqlSession session = sqlSessionFactory.openSession();

        try{
            session.insert("user.insertUser",user);
            session.commit();
        }finally {
            session.close();
        }

    }
}
