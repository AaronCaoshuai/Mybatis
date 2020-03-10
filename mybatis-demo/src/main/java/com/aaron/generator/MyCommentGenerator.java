package com.aaron.generator;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.InnerClass;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.internal.DefaultCommentGenerator;
import org.mybatis.generator.internal.util.StringUtility;

import java.util.Properties;

import static org.mybatis.generator.internal.util.StringUtility.isTrue;

/**
 * 自定义MBG的注释生成器
 */
public class MyCommentGenerator extends DefaultCommentGenerator {
    /**
     *  默认注释生成类没有提供属性访问方法 重新定义一次
     *  阻止生成注释 默认false
     */
    private boolean suppressAllComments;
    /**
     *  注释是否添加数据库表的备注信息
     */
    private boolean addRemarkComments;

    /**
     * 设置用户配置的参数
     * @param properties
     */
    public void addConfigurationProperties(Properties properties){
        //先调用父类方法保证父类方法可以正常使用
        super.addConfigurationProperties(properties);
        //获取suppressAllComments参数值
        suppressAllComments = isTrue(properties.getProperty(
                PropertyRegistry.COMMENT_GENERATOR_SUPPRESS_ALL_COMMENTS));
        //获取addRemarkComments
        addRemarkComments = isTrue(properties.getProperty(PropertyRegistry.COMMENT_GENERATOR_ADD_REMARK_COMMENTS));

    }

    /**
     * 对象头注释
     * @param topLevelClass
     * @param introspectedTable
     */
    public void addModelClassComment(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        //获取数据库表的注释 需要在数据库连接url中添加useInformationSchema=true
        if (!this.suppressAllComments && this.addRemarkComments) {
            StringBuilder sb = new StringBuilder();
            topLevelClass.addJavaDocLine("/**");
            topLevelClass.addJavaDocLine(" * ");
            String remarks = introspectedTable.getRemarks();
            if (this.addRemarkComments && StringUtility.stringHasValue(remarks)) {
                String[] remarkLines = remarks.split(System.getProperty("line.separator"));
                String[] arrs = remarkLines;
                int length = remarkLines.length;
                for(int i = 0; i < length; ++i) {
                    String remarkLine = arrs[i];
                    topLevelClass.addJavaDocLine(" * " + remarkLine);
                }
            }

            topLevelClass.addJavaDocLine(" * ");
            sb.append(" * " + introspectedTable.getFullyQualifiedTable());
            topLevelClass.addJavaDocLine(sb.toString());
            topLevelClass.addJavaDocLine(" * ");
            topLevelClass.addJavaDocLine(" **/");
        }
    }

    /**
     * 给字段添加注释信息
     * @param field
     * @param introspectedTable
     * @param introspectedColumn
     */
    public void addFieldComment(Field field, IntrospectedTable introspectedTable,
                                IntrospectedColumn introspectedColumn) {
        if(!this.suppressAllComments){
            //文档注释开始
            field.addJavaDocLine("/**");
            //获取数据库字段的备注信息
            String remarks = introspectedColumn.getRemarks();
            //根据参数和备注信息判断是否添加备注信息
            if(addRemarkComments && StringUtility.stringHasValue(remarks)){
                String[] remarkLines = remarks.split(System.getProperty("line.separator"));
                for(String remarkLine : remarkLines){
                    field.addJavaDocLine(" * " + remarkLine);
                }
            }
            //由于java对象名和数据库字段名可能不一样,注释中保留数据库字段名
            field.addJavaDocLine(" * " + introspectedColumn.getActualColumnName());
            field.addJavaDocLine(" */");
        }
    }


    /**
     * 去掉get方法注释
     * @param method
     * @param introspectedTable
     * @param introspectedColumn
     */
    public void addGetterComment(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {

    }

    /**
     * 去掉set方法注释
     * @param method
     * @param introspectedTable
     * @param introspectedColumn
     */
    public void addSetterComment(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {

    }

    /**
     * 去掉Mapper接口注释
     * @param method
     * @param introspectedTable
     */
    public void addGeneralMethodComment(Method method, IntrospectedTable introspectedTable) {

    }

    /**
     * Mapper.xml文件不添加注释
     * @param xmlElement
     */
    public void addComment(XmlElement xmlElement) {

    }

    /**
     * 插件相关的注释
     * @param field
     * @param introspectedTable
     */
    public void addFieldComment(Field field, IntrospectedTable introspectedTable) {

    }

    public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable, boolean markAsDoNotDelete) {

    }

    public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable) {

    }
}
