package com.aaron.jdbc;

import org.junit.Test;

import java.sql.*;

public class JDBCDemoTest {

    /**
     * 直接使用JDBC操作数据库
     */
    @Test
    public void testJDBCQuery(){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        try {
            // 加载数据库驱动
            Class.forName("com.mysql.jdbc.Driver");
            // 通过驱动管理类获取数据库链接connection = DriverManager
            connection = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/ssm?useUnicode=true&characterEncoding=utf-8&useInformationSchema=true",
                    "root",
                    "root");

            //定义sql语句?表示占位符
            String sql = "select * from user where username = ?";

            //获取预处理statement
            preparedStatement = connection.prepareStatement(sql);

            //设置参数,第一个参数为sql语句中参数的序号(从1开始),第二个参数为设置的
            preparedStatement.setString(1, "王五");

            // 向数据库发出 sql 执行查询，查询出结果集
            rs = preparedStatement.executeQuery();

            // 遍历查询结果集
            while (rs.next()) {
                System.out.println(rs.getString("id") + " " + rs.getString("username"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if(connection!=null){
                    connection.close();
                }
                if(preparedStatement != null){
                    preparedStatement.close();
                }
                if(rs != null){
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 使用简单封装获取连接和执行SQL
     */
    @Test
    public void testJDBCDemo(){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        try{

            //获取连接
            connection = JDBCUtil.getConnection();

            //定义sql语句?表示占位符
            String sql = "select * from user where username = ?";

            //获取预处理statement
            preparedStatement = connection.prepareStatement(sql);

            //设置参数,第一个参数为sql语句中参数的序号(从1开始),第二个参数为设置的
            preparedStatement.setString(1, "王五");

            // 向数据库发出 sql 执行查询，查询出结果集
            rs = preparedStatement.executeQuery();

            // 遍历查询结果集
            while (rs.next()) {
                System.out.println(rs.getString("id") + " " + rs.getString("username"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            JDBCUtil.releaseConnection(connection,preparedStatement,rs);
        }
    }
}
