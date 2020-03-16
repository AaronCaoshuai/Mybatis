/**
 *    Copyright 2009-2016 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.parsing;

import java.util.Properties;

/**
 * @author Clinton Begin
 * @author Kazuki Shimizu
 * 指定了是否开启使用默认值的功能以及默认的分隔符
 */
public class PropertyParser {
  //key的统一前缀
  private static final String KEY_PREFIX = "org.apache.ibatis.parsing.PropertyParser.";
  /**
   * The special property key that indicate whether enable a default value on placeholder.
   * <p>
   *   The default value is {@code false} (indicate disable a default value on placeholder)
   *   If you specify the {@code true}, you can specify key and default value on placeholder (e.g. {@code ${db.username:postgres}}).
   * </p>
   * @since 3.4.2
   */
  //在mybatis-config.xml中<properties>节点下配置是否开启默认值功能的对应配置项
  public static final String KEY_ENABLE_DEFAULT_VALUE = KEY_PREFIX + "enable-default-value";

  /**
   * The special property key that specify a separator for key and default value on placeholder.
   * <p>
   *   The default separator is {@code ":"}.
   * </p>
   * @since 3.4.2
   */
  //配置占位符与默认值之间分割付的对应配置项
  public static final String KEY_DEFAULT_VALUE_SEPARATOR = KEY_PREFIX + "default-value-separator";
  //默认情况下 关闭<properties>中的默认值的功能
  private static final String ENABLE_DEFAULT_VALUE = "false";
  //默认分割符是冒号
  private static final String DEFAULT_VALUE_SEPARATOR = ":";
  //构造器
  private PropertyParser() {
    // Prevent Instantiation
  }
  //创建GenericTokenParser解析器,
  public static String parse(String string, Properties variables) {
    VariableTokenHandler handler = new VariableTokenHandler(variables);
    GenericTokenParser parser = new GenericTokenParser("${", "}", handler);
    return parser.parse(string);
  }
  //私有静态内部类
  private static class VariableTokenHandler implements TokenHandler {
    private final Properties variables;//<properties>节点下定义的键值对,用于替换占位符
    private final boolean enableDefaultValue;//是否支持占位符中使用默认值的功能
    private final String defaultValueSeparator;//指定占位符和默认值之间的分隔符
    //构造器
    private VariableTokenHandler(Properties variables) {
      this.variables = variables;
      //优先设置配置文件中的属性否则设置为默认属性
      this.enableDefaultValue = Boolean.parseBoolean(getPropertyValue(KEY_ENABLE_DEFAULT_VALUE, ENABLE_DEFAULT_VALUE));
      this.defaultValueSeparator = getPropertyValue(KEY_DEFAULT_VALUE_SEPARATOR, DEFAULT_VALUE_SEPARATOR);
    }

    private String getPropertyValue(String key, String defaultValue) {
      return (variables == null) ? defaultValue : variables.getProperty(key, defaultValue);
    }
    //解析占位符
    @Override
    public String handleToken(String content) {
      if (variables != null) {//检测集合是否为空
        String key = content;
        if (enableDefaultValue) {//检测是否支持占位符中使用默认值的功能
          //查找分隔符
          final int separatorIndex = content.indexOf(defaultValueSeparator);
          String defaultValue = null;
          if (separatorIndex >= 0) {
            //获取占位符的名称
            key = content.substring(0, separatorIndex);
            //获取默认值
            defaultValue = content.substring(separatorIndex + defaultValueSeparator.length());
          }
          if (defaultValue != null) {
            //在variables集合中查找指定的占位符
            return variables.getProperty(key, defaultValue);
          }
        }
        //不支持默认值的功能,则直接查找variables集合
        if (variables.containsKey(key)) {
          return variables.getProperty(key);
        }
      }
      return "${" + content + "}";//variables集合为空
    }
  }

}
