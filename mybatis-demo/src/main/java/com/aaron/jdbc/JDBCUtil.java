package com.aaron.jdbc;

import java.sql.*;

/**
 * JDBC简单封装
 */
public class JDBCUtil {
    /**
     * 获取数据库连接
     * @return
     */
    public static Connection getConnection() {
        Connection connection = null;
        try {
            // 加载数据库驱动
            Class.forName("com.mysql.jdbc.Driver");
            // 通过驱动管理类获取数据库链接connection = DriverManager
            connection = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/ssm?useUnicode=true&characterEncoding=utf-8&useInformationSchema=true",
                    "root",
                    "root");
            return connection;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 释放数据库资源
     * @param connection
     * @param preparedStatement
     */
    public static void releaseConnection(Connection connection, Statement preparedStatement, ResultSet rs) {
        try {
            // 释放资源
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (connection != null) {
                connection.close();
            }
            if(rs != null){
                rs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
