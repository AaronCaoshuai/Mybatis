package com.aaron.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 反射Class类demo
 *
 * Class 类是对java类的描述,java程序中的各个java类属于同一个事物,描述这类的事物Java类就是Class
 * 每一份字节码就是一个Class对象
 * 创建Class对象的三种方式
 * 1.类名.class
 * 2.对象.getClass()
 * 3.Class.forName("全限定名")
 * java中预定义了九个Class对象
 * boolean byte short int long float double char void
 * 对于包装类型 例如:Integer.TYPE = int.class
 */
public class ReflectClassDemo {

    public static void main(String[] args) throws Exception {
        //类.class
        Class<Person> personClass = Person.class;
        System.out.println(personClass);
        //对象.getClass()
        Person person = new Person("张三","123456",23);
        Class<? extends Person> personClass1 = person.getClass();
        System.out.println(personClass1);
        //Class.forName("全限定名");
        Class<?> personClass2 = Class.forName("com.aaron.reflect.Person");
        System.out.println(personClass2);

        System.out.println(personClass == personClass1);//true 同一份字节码
        System.out.println(personClass == personClass2);//true 同一份字节码

        System.out.println("==================================");

        System.out.println(int.class == Integer.class);//false
        System.out.println(int.class == Integer.TYPE);//true
        Integer ii = 123;
        Integer iii = 345;
        System.out.println(ii.getClass() == iii.getClass());//true

        System.out.println("==================================");

        //Class中的api的使用
        //newInstance() 无参构造器创建对象
        Class<?> personClazz = Class.forName("com.aaron.reflect.Person");
        Person person1 = (Person)personClazz.newInstance();
        System.out.println(person1);

        //isPrimitive()指定类对象是否是一个基本类型
        System.out.println(personClass.isPrimitive());//false
        System.out.println(void.class.isPrimitive());//true
        System.out.println(int.class.isPrimitive());//true

        //getSuperclass() 返回指定类的父类对象
        System.out.println(personClass.getSuperclass());//class java.lang.Object

        //getPackage() 返回指定类所在的包
        System.out.println(personClass.getPackage());//package com.aaron.reflect

        //注解相关 已经获取类对象上的注解
        System.out.println(personClass.isAnnotation());//false 是否是一个注解类
        System.out.println(personClass.isAnnotationPresent(SuppressWarnings.class));//true Deprecated 的注解是否在该类上
        Annotation[] annotations = personClass.getAnnotations();
        System.out.println(annotations.length);
        Annotation[] declaredAnnotations = personClass.getDeclaredAnnotations();
        System.out.println(declaredAnnotations.length);
        SuppressWarnings annotation = personClass.getAnnotation(SuppressWarnings.class);
        System.out.println(annotation);
        SuppressWarnings[] annotationsByType = personClass.getAnnotationsByType(SuppressWarnings.class);
        System.out.println(annotationsByType);//[Ljava.lang.Deprecated;@763d9750

        //返回由Java语言规范定义的基础类的规范名称 getCanonicalName()
        System.out.println(personClass.getCanonicalName()); //com.aaron.reflect.Person

        //返回类加载器
        System.out.println(personClass.getClassLoader());//sun.misc.Launcher$AppClassLoader@18b4aac2

        //返回接口
        Class<?>[] interfaces = personClass.getInterfaces();
        System.out.println(interfaces.length);//1

        //getSimpleName()返回对象类的简单名称
        System.out.println("Class-simpleName:"+personClass.getSimpleName());//Class-simpleName:Person
        //getName() 返回对象类的全限定名称
        System.out.println("Class-name:"+personClass.getName());//Class-name:com.aaron.reflect.Person

        //返回该类或者接口的java语言修饰符,以整数编码
        int modifiers = personClass.getModifiers();
        System.out.println(modifiers);//1

        //Declared开头的方法表示可以返回私有的
        //返回构造器 或者根据参数列表类型返回构造器
        Constructor<?>[] constructors = personClass.getConstructors();
        System.out.println(constructors.length);//2
        Constructor<?>[] declaredConstructors = personClass.getDeclaredConstructors();
        System.out.println(declaredConstructors.length);//3
        Constructor<Person> constructor = personClass.getConstructor(null);
        System.out.println(constructor);
        Constructor<Person> personClassConstructor = personClass.getConstructor(String.class, String.class, int.class);
        System.out.println(personClassConstructor);
        Constructor<Person> declaredConstructor = personClass.getDeclaredConstructor(String.class, int.class);
        System.out.println(declaredConstructor);

        //返回字段
        Field[] fields = personClass.getFields();
        System.out.println(fields.length);//1
        Field[] declaredFields = personClass.getDeclaredFields();
        System.out.println(declaredFields.length);//3
        Field username = personClass.getField("username");
        System.out.println(username);
        Field declaredField = personClass.getDeclaredField("password");
        System.out.println(declaredField);

        //返回方法
        //返回包含一个数组 方法对象反射由此表示的类或接口的所有公共方法 类对象，包括那些由类或接口和那些从超类和超接口继承的声明。
        Method[] methods = personClass.getMethods();
        System.out.println(methods.length);//10
        //方法对象反射的类或接口的所有声明的方法，通过此表示 类对象，包括公共，保护，默认（包）访问和私有方法，但不包括继承的方法。
        Method[] declaredMethods = personClass.getDeclaredMethods();
        System.out.println(declaredMethods.length);//2
        Method run = personClass.getMethod("run");
        System.out.println(run);
        Method run1 = personClass.getDeclaredMethod("run");
        System.out.println(run1);

    }

}
