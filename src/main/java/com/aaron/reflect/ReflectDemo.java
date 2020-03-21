package com.aaron.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Date;

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
public class ReflectDemo {

    public static void main(String[] args) throws Exception {
        /**
         * 获取Class对象三种方式
         */
        //类.class
        Class<Student> studentClass = Student.class;
        System.out.println(studentClass);
        //对象.getClass()
        Student student = new Student();
        Class<? extends Student> studentClass1 = student.getClass();
        System.out.println(studentClass1);
        //Class.forName("全限定名");
        Class<?> studentClass2 = Class.forName("com.aaron.reflect.Student");
        System.out.println(studentClass2);

        System.out.println(studentClass == studentClass1);//true 同一份字节码
        System.out.println(studentClass == studentClass2);//true 同一份字节码

        System.out.println("==================================");

        System.out.println(int.class == Integer.class);//false
        System.out.println(int.class == Integer.TYPE);//true
        Integer ii = 123;
        Integer iii = 345;
        System.out.println(ii.getClass() == iii.getClass());//true

        System.out.println("==================================");

        //Class中的api的使用
        //newInstance() 无参构造器创建对象
        Class<?> studentClazz = Class.forName("com.aaron.reflect.Student");
        Student student1 = (Student)studentClazz.newInstance();
        System.out.println(student1);
        System.out.println("==================================");

        //isPrimitive()指定类对象是否是一个基本类型
        System.out.println(studentClazz.isPrimitive());//false
        System.out.println(void.class.isPrimitive());//true
        System.out.println(int.class.isPrimitive());//true
        System.out.println("==================================");

        //getPackage() 返回指定类所在的包
        System.out.println(studentClazz.getPackage());//package com.aaron.reflect
        System.out.println("==================================");

        //isAnonymousClass() 查看是否是匿名内部类
        System.out.println(studentClass.isAnonymousClass());//false

        //isInterface()是否是接口
        System.out.println(studentClass.isInterface());//false

        //isMemberClass() 是否是抽象类
        System.out.println(studentClass.isMemberClass());//false
        System.out.println("==================================");


        //注解相关 已经获取类对象上的注解
        System.out.println(studentClazz.isAnnotation());//false 是否是一个注解类
        System.out.println(studentClazz.isAnnotationPresent(SuppressWarnings.class));//true Deprecated 的注解是否在该类上
        Annotation[] annotations = studentClazz.getAnnotations();
        System.out.println(annotations.length);
        Annotation[] declaredAnnotations = studentClazz.getDeclaredAnnotations();
        System.out.println(declaredAnnotations.length);
        SuppressWarnings annotation = studentClazz.getAnnotation(SuppressWarnings.class);
        System.out.println(annotation);
        SuppressWarnings[] annotationsByType = studentClazz.getAnnotationsByType(SuppressWarnings.class);
        System.out.println(annotationsByType);//[Ljava.lang.Deprecated;@763d9750
        System.out.println("==================================");

        //getSuperclass() 返回指定类的父类对象
        System.out.println("student 类的父类 " + studentClazz.getSuperclass());//class java.lang.Object
        System.out.println("==================================");

        //返回接口
        Class<?>[] interfaces = studentClazz.getInterfaces();
        System.out.println(interfaces.length);//1
        System.out.println("==================================");

        //返回由Java语言规范定义的基础类的规范名称 getCanonicalName()
        System.out.println(studentClazz.getCanonicalName()); //com.aaron.reflect.Student
        //getSimpleName()返回对象类的简单名称
        System.out.println("Class-simpleName:"+studentClazz.getSimpleName());//Class-simpleName:Student
        //getName() 返回对象类的全限定名称
        System.out.println("Class-name:"+studentClazz.getName());//Class-name:com.aaron.reflect.Student
        System.out.println("==================================");

        //返回该类或者接口的java语言修饰符,以整数编码
        int modifiers = studentClazz.getModifiers();
        System.out.println(modifiers);//1
        System.out.println("==================================");

        //返回类加载器
        System.out.println(studentClazz.getClassLoader());//sun.misc.Launcher$AppClassLoader@18b4aac2
        System.out.println("==================================");
        /**
         * 1，当我们拿到一个Class，用Class. getGenericInterfaces()方法得到Type[]，也就是这个类实现接口的Type类型列表。
         */
        //获取该类的接口类型
        Type[] genericInterfaces = studentClazz.getGenericInterfaces();
        for (Type genericInterfaceType : genericInterfaces){
            System.out.println("类实现接口的Type类型列表:" + genericInterfaceType);
            //类实现接口的Type类型列表:interface com.aaron.reflect.PersonInterface
            //类实现接口的Type类型列表:interface java.io.Serializable
        }
        System.out.println("==================================");


        /**
         * 构造器 字段 方法
         */
        //Declared开头的方法表示可以返回私有的
        //返回构造器 或者根据参数列表类型返回构造器
        Constructor<?>[] constructors = studentClazz.getConstructors();
        for (Constructor constructor : constructors){
            System.out.println(constructor);
            //public com.aaron.reflect.Student(int,java.util.Date,java.lang.Double)
            //public com.aaron.reflect.Student()
        }

        Constructor<?>[] declaredConstructors = studentClazz.getDeclaredConstructors();
        for (Constructor declaredConstructor : declaredConstructors){
            System.out.println(declaredConstructor);
            //private com.aaron.reflect.Student(int)
            //public com.aaron.reflect.Student(int,java.util.Date,java.lang.Double)
            //public com.aaron.reflect.Student()
        }

        Constructor<Student> constructor = (Constructor<Student>) studentClazz.getConstructor(null);
        System.out.println(constructor);//public com.aaron.reflect.Student()
        Student student2 = constructor.newInstance();
        System.out.println(student2);//Student{grade=0, birthday=null, mark=null}
        Constructor<Student> studentClassConstructor = (Constructor<Student>) studentClazz.getConstructor(int.class, Date.class, Double.class);
        Student student3 = studentClassConstructor.newInstance(100, new Date(), 87.5);
        System.out.println(student3);//Student{grade=100, birthday=Thu Mar 19 17:40:03 CST 2020, mark=87.5}


        Constructor<Student> declaredConstructor = (Constructor<Student>) studentClazz.getDeclaredConstructor(int.class);
        declaredConstructor.setAccessible(true);
        Student student4 = declaredConstructor.newInstance(100);
        System.out.println(student4);//Student{grade=100, birthday=null, mark=null}
        System.out.println("==================================");

        /**
         * 2，当我们拿到一个Class，用Class.getDeclaredFields()方法得到Field[]，也就是类的属性列表，然后用Field. getGenericType()方法得到这个属性的Type类型。
         *
         * 3，当我们拿到一个Method，用Method. getGenericParameterTypes()方法获得Type[]，也就是方法的参数类型列表。
         */
        //返回字段
        Field[] fields = studentClazz.getFields();
        for (Field field : fields){
            System.out.println(field.getName());
            Type genericType = field.getGenericType();
            System.out.println("字段类型:"+genericType.getTypeName());
            //sex
            //字段类型:java.lang.String
        }
        System.out.println("分割字段返回信息");
        Field[] declaredFields = studentClazz.getDeclaredFields();
        for (Field declaredField : declaredFields){
            declaredField.setAccessible(true);
            System.out.println(declaredField.getName());
            Type genericType = declaredField.getGenericType();
            System.out.println("字段类型:"+genericType.getTypeName());
            //grade
            //字段类型:int
            //birthday
            //字段类型:java.util.Date
            //mark
            //字段类型:java.lang.Double
            //sex
            //字段类型:java.lang.String
            //hight
            //字段类型:int
        }
        Student fieldStudent = new Student(123,null,null);
        System.out.println(fieldStudent);//Student{grade=123, birthday=null, mark=null}
        Field gradeField = studentClazz.getDeclaredField("grade");
        gradeField.setAccessible(true);
        System.out.println(gradeField.get(fieldStudent));//100
        gradeField.set(fieldStudent,100);
        System.out.println(fieldStudent);//Student{grade=100, birthday=null, mark=null}
        System.out.println("==================================");

        //返回方法
        //返回包含一个数组 方法对象反射由此表示的类或接口的所有公共方法 类对象，包括那些由类或接口和那些从超类和超接口继承的声明。
        Student methodStudent = new Student(123,null,null);
        Method[] methods = studentClazz.getMethods();
        for (Method method : methods){
            System.out.println("方法名称:"+method.getName());
            Type[] genericParameterTypes = method.getGenericParameterTypes();
            for (int i = 0;i<genericParameterTypes.length;i++){
                System.out.println("方法参数第"+(i+1)+"类型是:"+genericParameterTypes[i].getTypeName());
            }
            Type genericReturnType = method.getGenericReturnType();
            System.out.println("方法的返回类型:"+genericReturnType.getTypeName());
            Type[] genericExceptionTypes = method.getGenericExceptionTypes();
            for(int i = 0;i<genericExceptionTypes.length;i++){
                System.out.println("方法抛出的异常第"+(i+1)+"异常类型是"+genericExceptionTypes[i].getTypeName());
            }
        }
        System.out.println("方法分割符号");
        //获取当前类的各种方法 包括私有方法
        Method[] declaredMethods = studentClazz.getDeclaredMethods();
        for (Method declaredMethod : declaredMethods){
            declaredMethod.setAccessible(true);
            System.out.println("方法名称:"+declaredMethod.getName());
            Type[] genericParameterTypes = declaredMethod.getGenericParameterTypes();
            for (int i = 0;i<genericParameterTypes.length;i++){
                System.out.println("方法参数第"+(i+1)+"类型是:"+genericParameterTypes[i].getTypeName());
            }
            Type genericReturnType = declaredMethod.getGenericReturnType();
            System.out.println("方法的返回类型:"+genericReturnType.getTypeName());
            Type[] genericExceptionTypes = declaredMethod.getGenericExceptionTypes();
            for(int i = 0;i<genericExceptionTypes.length;i++){
                System.out.println("方法抛出的异常第"+(i+1)+"异常类型是"+genericExceptionTypes[i].getTypeName());
            }
        }

        Method goPartyMethod = studentClazz.getDeclaredMethod("goParty");
        goPartyMethod.setAccessible(true);
        Object retVal = goPartyMethod.invoke(methodStudent);
        System.out.println(retVal);//go Party

    }

}
