package com.aaron.mybatisdemo;

import com.aaron.generator.domain.OrderDetail;
import com.aaron.generator.mapper.OrderDetailMapper;
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

import java.util.ArrayList;
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
     * 对象参数传递测试
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
            System.out.println("特定级联关系子查询不开启懒加载"+users);
        }finally{
            session.close();
        }
    }

    /**
     * 批量插入 动态SQL
     * @throws Exception
     */
    @Test
    public void testBatchInsertOrdres() throws Exception{
        SqlSession session = sqlSessionFactory.openSession();
        List<Order> orders = new ArrayList<Order>();
        for (int i = 0;i<10;i++){
            Order order = new Order();
            order.setRemark("批量插入订单"+i);
            order.setCreateTime(new Date());
            order.setUpdateTime(new Date());
            order.setUserId(15);
            orders.add(order);
        }
        try{
            OrderMapper orderMapper = session.getMapper(OrderMapper.class);
            orderMapper.batchInsertOrdres(orders);

            System.out.println(orders);
            session.commit();
        }finally{
            session.close();
        }
    }

    /**
     * 测试一级缓存（session级别缓存 默认开启）
     *
     * @throws Exception
     */
    @Test
    public void testSessionCache() throws Exception{
        SqlSession session = sqlSessionFactory.openSession();
        UserQueryVO queryVO = new UserQueryVO();
        queryVO.setAddress("上海浦东");
        queryVO.setSex("女");
        try{
            UserMapper userMapper = session.getMapper(UserMapper.class);
            List<User> users1 = userMapper.queryUsersByCondition(queryVO);
            System.out.println("第一次查询没有缓存："+users1);
            //如果是执行两次service调用查询相同 的用户信息，是不走一级缓存的，因为mapper方法结束，
            //sqlSession就关闭，一级缓存就清空。
            List<User> users2 = userMapper.queryUsersByCondition(queryVO);
            System.out.println("同一个session（session级别）里查询一级缓存："+users2);

            User user = new User();
            user.setUsername("王五");
            user.setBirthday(new Date());
            user.setAddress("上海浦东");
            user.setSex("女");
            userMapper.insertUser(user);
            System.out.println("insert update delete 删除一级缓存数据");

            List<User> users3 = userMapper.queryUsersByCondition(queryVO);
            System.out.println("缓存重新加载："+users3);
            newSessionCache(queryVO);
            session.commit();
        }finally{
            session.close();
        }
    }

    /**
     * 不同的session的
     * @param queryVO
     * @throws Exception
     */
    private void newSessionCache(UserQueryVO queryVO) throws Exception{
        SqlSession session = sqlSessionFactory.openSession();
        UserMapper userMapper = session.getMapper(UserMapper.class);
        List<User> users3 = userMapper.queryUsersByCondition(queryVO);
        System.out.println("不同session（session级别）缓存不共享："+users3);
    }


    /**
     * 测试二级缓存（namespace级别缓存 默认开启）
     *
     * @throws Exception
     */
    @Test
    public void testMapperCache() throws Exception{
        SqlSession session1 = sqlSessionFactory.openSession();
        SqlSession session2 = sqlSessionFactory.openSession();
        SqlSession session3 = sqlSessionFactory.openSession();
        SqlSession session4 = sqlSessionFactory.openSession();
        UserQueryVO queryVO = new UserQueryVO();
        queryVO.setAddress("上海浦东");
        queryVO.setSex("女");

        UserMapper userMapper1 = session1.getMapper(UserMapper.class);
        List<User> users1 = userMapper1.queryUsersByCondition(queryVO);
        System.out.println("第一次查询没有命中二级缓存："+users1);
        session1.close();
        //如果是执行两次service调用查询相同 的用户信息，是不走一级缓存的，因为mapper方法结束，
        //sqlSession就关闭，一级缓存就清空。
        UserMapper userMapper2 = session2.getMapper(UserMapper.class);
        List<User> users2 = userMapper2.queryUsersByCondition(queryVO);
        System.out.println("第二次查询命中二级缓存"+users2);
        session2.close();

        UserMapper userMapper3 = session3.getMapper(UserMapper.class);
        User user = new User();
        user.setUsername("王五");
        user.setBirthday(new Date());
        user.setAddress("上海浦东");
        user.setSex("女");
        userMapper3.insertUser(user);
        System.out.println("insert update delete 删除二级缓存数据");
        session3.commit();

        UserMapper userMapper4 = session4.getMapper(UserMapper.class);
        List<User> users4 = userMapper4.queryUsersByCondition(queryVO);
        System.out.println("二级缓存未命中："+users4);
        session4.close();

    }

    /**
     * 测试mapper指定语句不使用二级缓存（namespace级别缓存 默认开启）
     *
     * @throws Exception
     */
    @Test
    public void testUserNotMapperCache() throws Exception{
        SqlSession session1 = sqlSessionFactory.openSession();
        SqlSession session2 = sqlSessionFactory.openSession();
        UserQueryVO queryVO = new UserQueryVO();
        queryVO.setAddress("上海浦东");
        queryVO.setSex("女");

        UserMapper userMapper1 = session1.getMapper(UserMapper.class);
        List<User> users1 = userMapper1.queryUsersByCondition(queryVO);
        System.out.println("第一次查询没有命中二级缓存,且没有加入二级缓存："+users1);
        session1.close();
        //如果是执行两次service调用查询相同 的用户信息，是不走一级缓存的，因为mapper方法结束，
        //sqlSession就关闭，一级缓存就清空。
        UserMapper userMapper2 = session2.getMapper(UserMapper.class);
        List<User> users2 = userMapper2.queryUsersByCondition(queryVO);
        System.out.println("第二次查询没有命中二级缓存"+users2);
        session2.close();

    }

    @Test
    public void testGeneratorMapper() {
        SqlSession session = sqlSessionFactory.openSession();
        OrderDetailMapper orderDetailMapper = session.getMapper(OrderDetailMapper.class);
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setCreateTime(new Date());
        orderDetail.setUpdateTime(new Date());
        orderDetail.setCreateUserId(1);
        orderDetail.setUpdateUserId(1);
        orderDetail.setVersion(1);
        orderDetail.setOrderId(15);
        orderDetail.setRemark("插入订单明细");
        orderDetailMapper.insert(orderDetail);
        System.out.println("insert :" + orderDetail);

        orderDetail.setRemark("修改测试");
        orderDetailMapper.updateByPrimaryKey(orderDetail);
        System.out.println("update :" + orderDetail);

        OrderDetail orderDetail1 = orderDetailMapper.selectByPrimaryKey(orderDetail.getId());
        System.out.println("主键查询 :" + orderDetail1);

        List<OrderDetail> orderDetails = orderDetailMapper.selectAll();
        System.out.println("列表查询 :" + orderDetails);

        orderDetailMapper.deleteByPrimaryKey(orderDetail.getId());

        session.commit();
        session.close();
    }

    @Test
    public void testGeneratorMapperExt() {
        SqlSession session = sqlSessionFactory.openSession();
        OrderDetailMapper orderDetailMapper = session.getMapper(OrderDetailMapper.class);
        List<OrderDetail> orderDetails = orderDetailMapper.selectAllExt();
        System.out.println(orderDetails);
        session.close();
    }

}
