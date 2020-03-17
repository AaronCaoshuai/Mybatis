/**
 *    Copyright 2009-2018 the original author or authors.
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
package org.apache.ibatis.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

import org.apache.ibatis.reflection.invoker.GetFieldInvoker;
import org.apache.ibatis.reflection.invoker.Invoker;
import org.apache.ibatis.reflection.invoker.MethodInvoker;
import org.apache.ibatis.reflection.property.PropertyTokenizer;

/**
 * @author Clinton Begin
 * 类级别的元信息的封装和处理
 * 实现了对复杂属性表达式的解析 并实现了获取指定属性描述信息的功能
 */
public class MetaClass {
  //ReflectorFactory对象,用于缓存Reflector对象
  private final ReflectorFactory reflectorFactory;
  //创建MetaClass时会指定一个类,该Reflector对象会用于记录该类的相关的元信息
  private final Reflector reflector;
  //MetaClass的构造方法是使用private修饰的
  private MetaClass(Class<?> type, ReflectorFactory reflectorFactory) {
    this.reflectorFactory = reflectorFactory;
    //创建Reflector对象
    this.reflector = reflectorFactory.findForClass(type);
  }
  //使用静态方法创建MetaClass对象
  public static MetaClass forClass(Class<?> type, ReflectorFactory reflectorFactory) {
    return new MetaClass(type, reflectorFactory);
  }
  //为对应的属性创建MetaClass对象
  public MetaClass metaClassForProperty(String name) {
    //查找指定属性对应的Class
    Class<?> propType = reflector.getGetterType(name);
    return MetaClass.forClass(propType, reflectorFactory);
  }
  //通过调用MetaClass.builProperty()方法实现的 只查找"."导航的属性,并没有检查下标
  public String findProperty(String name) {
    StringBuilder prop = buildProperty(name, new StringBuilder());
    return prop.length() > 0 ? prop.toString() : null;
  }
  //是否忽略"_"分隔符
  public String findProperty(String name, boolean useCamelCaseMapping) {
    if (useCamelCaseMapping) {
      name = name.replace("_", "");
    }
    return findProperty(name);
  }

  public String[] getGetterNames() {
    return reflector.getGetablePropertyNames();
  }

  public String[] getSetterNames() {
    return reflector.getSetablePropertyNames();
  }

  public Class<?> getSetterType(String name) {
    PropertyTokenizer prop = new PropertyTokenizer(name);
    if (prop.hasNext()) {
      MetaClass metaProp = metaClassForProperty(prop.getName());
      return metaProp.getSetterType(prop.getChildren());
    } else {
      return reflector.getSetterType(prop.getName());
    }
  }
  //逻辑与hasGetter()类似
  public Class<?> getGetterType(String name) {
    PropertyTokenizer prop = new PropertyTokenizer(name);//解析属性表达式
    if (prop.hasNext()) {
      MetaClass metaProp = metaClassForProperty(prop);//调用metaClassForProperty()方法
      return metaProp.getGetterType(prop.getChildren());//递归调用
    }
    // issue #506. Resolve the type inside a Collection Object
    return getGetterType(prop);//调用getGetterType(PropertyTokenizer) 重载
  }

  private MetaClass metaClassForProperty(PropertyTokenizer prop) {
    Class<?> propType = getGetterType(prop);//获取表达式所表示的属性的类型
    return MetaClass.forClass(propType, reflectorFactory);
  }

  private Class<?> getGetterType(PropertyTokenizer prop) {
    //获取属性类型
    Class<?> type = reflector.getGetterType(prop.getName());
    //该表达式中是否使用了"[]"指定了下标,且是Collection子类
    if (prop.getIndex() != null && Collection.class.isAssignableFrom(type)) {
      //通过TypeParameterResolver工具解析属性的类型
      Type returnType = getGenericGetterType(prop.getName());
      //针对ParameterizedType进行处理,即针对泛型集合类型进行处理
      if (returnType instanceof ParameterizedType) {
        Type[] actualTypeArguments = ((ParameterizedType) returnType).getActualTypeArguments();
        if (actualTypeArguments != null && actualTypeArguments.length == 1) {
          returnType = actualTypeArguments[0];//泛型的类型
          if (returnType instanceof Class) {
            type = (Class<?>) returnType;
          } else if (returnType instanceof ParameterizedType) {
            type = (Class<?>) ((ParameterizedType) returnType).getRawType();
          }
        }
      }
    }
    return type;
  }

  private Type getGenericGetterType(String propertyName) {
    try {
      //根据Reflector.getMethods集合中记录的Invoker实现类的类型,决定解析getter方法返回值
      //类型还是解析字段类型
      Invoker invoker = reflector.getGetInvoker(propertyName);
      if (invoker instanceof MethodInvoker) {
        Field _method = MethodInvoker.class.getDeclaredField("method");
        _method.setAccessible(true);
        Method method = (Method) _method.get(invoker);
        return TypeParameterResolver.resolveReturnType(method, reflector.getType());
      } else if (invoker instanceof GetFieldInvoker) {
        Field _field = GetFieldInvoker.class.getDeclaredField("field");
        _field.setAccessible(true);
        Field field = (Field) _field.get(invoker);
        return TypeParameterResolver.resolveFieldType(field, reflector.getType());
      }
    } catch (NoSuchFieldException | IllegalAccessException ignored) {
    }
    return null;
  }

  /**
   * hasSetter() hasGetter() 需要注意某些没有get set 方法的字段
   * @param name
   * @return
   */
  //判断属性表达式所表示的属性是否有对应的set方法
  public boolean hasSetter(String name) {
    PropertyTokenizer prop = new PropertyTokenizer(name);
    if (prop.hasNext()) {
      if (reflector.hasSetter(prop.getName())) {
        MetaClass metaProp = metaClassForProperty(prop.getName());
        return metaProp.hasSetter(prop.getChildren());//递归入口
      } else {
        return false;//递归出口
      }
    } else {
      return reflector.hasSetter(prop.getName());//递归出口
    }
  }
  //判断属性表达式所表示的属性是否有对应的get方法
  public boolean hasGetter(String name) {
    PropertyTokenizer prop = new PropertyTokenizer(name);
    if (prop.hasNext()) {
      if (reflector.hasGetter(prop.getName())) {
        //metaClassForProperty(prop) 重载方法
        MetaClass metaProp = metaClassForProperty(prop);
        return metaProp.hasGetter(prop.getChildren());
      } else {
        return false;
      }
    } else {
      return reflector.hasGetter(prop.getName());
    }
  }

  public Invoker getGetInvoker(String name) {
    return reflector.getGetInvoker(name);
  }

  public Invoker getSetInvoker(String name) {
    return reflector.getSetInvoker(name);
  }

  private StringBuilder buildProperty(String name, StringBuilder builder) {
    PropertyTokenizer prop = new PropertyTokenizer(name);//解析属性表达式
    if (prop.hasNext()) {//是否有子表达式
      //查找PropertyTokenizer.name对应的属性
      String propertyName = reflector.findPropertyName(prop.getName());
      if (propertyName != null) {
        builder.append(propertyName);//追加属性名
        builder.append(".");
        //为该属性创建对应的MetaClass对象
        MetaClass metaProp = metaClassForProperty(propertyName);
        //递归解析PropertyTokenizer.children字段,并将解析结果追加到builder中保存
        metaProp.buildProperty(prop.getChildren(), builder);
      }
    } else {//递归出口
      String propertyName = reflector.findPropertyName(name);
      if (propertyName != null) {
        builder.append(propertyName);
      }
    }
    return builder;
  }

  public boolean hasDefaultConstructor() {
    return reflector.hasDefaultConstructor();
  }

}
