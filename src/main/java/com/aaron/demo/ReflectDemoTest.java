package com.aaron.demo;

import java.io.InputStream;
import java.util.Properties;

/**
 * 使用反射读取配置文件创建User类
 */
public class ReflectDemoTest {

    public static void main(String[] args) throws Exception {
        InputStream resource = ClassLoader.getSystemResourceAsStream("config.properties");
        Properties properties = new Properties();
        properties.load(resource);
        resource.close();
        String className = properties.getProperty("className");
        Class<?> clazz = Class.forName(className);
        Object obj = clazz.newInstance();
        System.out.println(obj);
    }
}
