package com.aaron.generator;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 代码生成器类MBG
 */
public class Generator {

    public static void main(String[] args) throws Exception{
        //MBG执行过程中的警告信息
        List<String> warnings = new ArrayList();
        //当生成的代码重复是,覆盖原代码
        boolean overwrite = true;
        //读取MBG配置文件
        InputStream input =  Generator.class.getResourceAsStream("/generatorConfig.xml");
        ConfigurationParser configurationParser = new ConfigurationParser(warnings);
        Configuration configuration = configurationParser.parseConfiguration(input);
        input.close();

        DefaultShellCallback callback = new DefaultShellCallback(overwrite);
        //创建MBG
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(configuration,callback,warnings);
        //执行MBG生成代码
        myBatisGenerator.generate(null);
        //输出MBG警告信息
        for (String waring : warnings){
            System.out.println(waring);
        }
    }
}
