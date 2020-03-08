package com.aaron.mybatisdemo;

import com.aaron.mybatisdemo.dao.UserDao;
import com.aaron.mybatisdemo.domain.Order;
import com.aaron.mybatisdemo.domain.User;
import com.aaron.mybatisdemo.impl.UserDaoImpl;
import com.aaron.mybatisdemo.mapper.AnnotationUserMapper;
import com.aaron.mybatisdemo.mapper.OrderMapper;
import com.aaron.mybatisdemo.mapper.UserMapper;
import com.aaron.mybatisdemo.queryvo.UserQueryVO;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.List;

/**
 * 单元测试类
 */
public class MybatisDemoTest {

    private SqlSessionFactory sqlSessionFactory;

    private UserDao userDao;


    /**
     * 初始化sqlSessionFactory
     * @throws Exception
     */
    @Before
    public void init() throws Exception{
        //SqlSessionFactory的构建器 Resources 类
        SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
        sqlSessionFactory = sqlSessionFactoryBuilder.build(Resources.getResourceAsStream("SqlMapConfig.xml"));
        userDao = new UserDaoImpl(sqlSessionFactory);
    }

    /**
     * 测试项目基础查询功能
     * @throws Exception
     */
    @Test
    public void testFindUserById() throws Exception{
        User user = userDao.findUserById(1);
        System.out.println(user);
        User user2 = userDao.findUserById(3);
        System.out.println(user2);
    }

    /**
     * 测试项目基础查询功能
     * @throws Exception
     */
    @Test
    public void testFindUserByName() throws Exception{
        List<User> users = userDao.findUserByName("李");
        System.out.println(users);
    }

    /**
     * 测试项目基础插入功能
     * @throws Exception
     */
    @Test
    public void testInsertUser() throws Exception{
        User user = new User();
        user.setUsername("王五");
        user.setBirthday(new Date());
        user.setAddress("上海浦东");
        user.setSex("女");
        System.out.println("主键未返回:"+user);
        userDao.insertUser(user);
        System.out.println("主键返回:"+user);
    }

    /**
     * 使用动态代理mapper接口模式开发
     * @throws Exception
     */
    @Test
    public void testXmlProxyMapper() throws Exception{
        SqlSession session = sqlSessionFactory.openSession();
        try{
            UserMapper userMapper = session.getMapper(UserMapper.class);
            User user = userMapper.findUserById(4);
            System.out.println(user);
            List<User> users = userMapper.findUserByName("李");
            System.out.println(users);
            User insertUser = new User();
            insertUser.setUsername("王五代理插入");
            insertUser.setBirthday(new Date());
            insertUser.setAddress("上海浦东");
            insertUser.setSex("女");
            userMapper.insertUser(insertUser);
            System.out.println("mapper代理插入"+insertUser);
            userMapper.deleteUserById(3);
            session.commit();
        }finally{
            session.close();
        }
    }

    /**
     * 使用注解sql方式开发
     * @throws Exception
     */
    @Test
    public void testAnnotationProxyMapper() throws Exception{
        SqlSession session = sqlSessionFactory.openSession();
        User insertUser = new User();
        insertUser.setUsername("王五Annotation代理插入");
        insertUser.setBirthday(new Date());
        insertUser.setAddress("上海浦东");
        insertUser.setSex("女");
        try{
            AnnotationUserMapper userMapper = session.getMapper(AnnotationUserMapper.class);
            User user = userMapper.findUserById(4);
            System.out.println(user);
            List<User> users = userMapper.findUserByName("李");
            System.out.println(users);
            userMapper.insertUser(insertUser);
            System.out.println("mapper代理插入"+insertUser);
            userMapper.deleteUserById(2);
            session.commit();
        }finally{
            session.close();
        }
    }

    /**
     * 对象传递测试
     * @throws Exception
     */
    @Test
    public void testQueryUsersByCondition() throws Exception{
        SqlSession session = sqlSessionFactory.openSession();
        UserQueryVO queryVO = new UserQueryVO();
        queryVO.setAddress("上海浦东");
        queryVO.setSex("女");
        try{
            UserMapper userMapper = session.getMapper(UserMapper.class);
            List<User> users = userMapper.queryUsersByCondition(queryVO);
            System.out.println(users);
        }finally{
            session.close();
        }
    }

    /**
     * 输入和输出类型是基本类型
     * @throws Exception
     */
    @Test
    public void testSelectUserCount() throws Exception{
        SqlSession session = sqlSessionFactory.openSession();
        UserQueryVO queryVO = new UserQueryVO();
        queryVO.setAddress("上海浦东");
        queryVO.setSex("女");
        try{
            UserMapper userMapper = session.getMapper(UserMapper.class);
            int userCount = userMapper.selectUserCount(queryVO);
            System.out.println(userCount);
        }finally{
            session.close();
        }
    }

    /**
     * 一对多结果映射集测试resultMap
     * @throws Exception
     */
    @Test
    public void testSelectOrdersByUserId() throws Exception{
        SqlSession session = sqlSessionFactory.openSession();
        try{
            UserMapper userMapper = session.getMapper(UserMapper.class);
            User user = userMapper.selectOrdersByUserId(15);
            System.out.println(user);
        }finally{
            session.close();
        }
    }

    /**
     * 一对一结果映射集测试resultMap
     * @throws Exception
     */
    @Test
    public void testSelectUserByOrderId() throws Exception{
        SqlSession session = sqlSessionFactory.openSession();
        try{
            OrderMapper orderMapper = session.getMapper(OrderMapper.class);
            Order order = orderMapper.selectUserByOrderId(1);
            System.out.println(order);
        }finally{
            session.close();
        }
    }

    /**
     * 级联查询n+1问题 解决方案见userMapper.xml中描述
     * @throws Exception
     */
    @Test
    public void testSelectAllUserOrders() throws Exception{
        SqlSession session = sqlSessionFactory.openSession();
        try{
            UserMapper userMapper = session.getMapper(UserMapper.class);
            List<User> users = userMapper.selectAllUserOrders();
            System.out.println(users);
        }finally{
            session.close();
        }
    }

    /**
     *  延迟加载开启 侵入式延迟加载关闭 测试
     * @throws Exception
     */
    @Test
    public void testAggressiveLazyLoadingFalse() throws Exception{
        SqlSession session = sqlSessionFactory.openSession();
        try{
            UserMapper userMapper = session.getMapper(UserMapper.class);
            List<User> users = userMapper.selectAllUserOrders();
            System.out.println(users);
            for (User user : users){
                System.out.println("深度延迟加载"+user.getOrders());
            }
        }finally{
            session.close();
        }
    }

    /**
     *  延迟加载开启 侵入式延迟加载开启 测试
     * @throws Exception
     */
    @Test
    public void testAggressiveLazyLoadingTrue() throws Exception{
        SqlSession session = sqlSessionFactory.openSession();
        try{
            UserMapper userMapper = session.getMapper(UserMapper.class);
            List<User> users = userMapper.selectAllUserOrders();
            for (User user : users){
                System.out.println("侵入式延迟加载"+user.getId());
            }
        }finally{
            session.close();
        }
    }

    /**
     *  延迟加载开启 对于特定级联查询不开启懒加载 测试
     * @throws Exception
     */
    @Test
    public void testFetchTypeEager() throws Exception{
        SqlSession session = sqlSessionFactory.openSession();
        try{
            UserMapper userMapper = session.getMapper(UserMapper.class);
            System.out.println("特定级联关系子查询不开启懒加载");
            List<User> users = userMapper.selectAllUserOrders();
            System.out.println("特定级联关系子查询不开启懒加载");
        }finally{
            session.close();
        }
    }
}
