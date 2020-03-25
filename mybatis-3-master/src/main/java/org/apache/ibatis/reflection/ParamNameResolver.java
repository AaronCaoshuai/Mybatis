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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.binding.MapperMethod.ParamMap;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

/**
 * 处理Mapper接口中定义的方法的参数列表
 */
public class ParamNameResolver {

  private static final String GENERIC_NAME_PREFIX = "param";

  /**
   * <p>
   * The key is the index and the value is the name of the parameter.<br />
   * The name is obtained from {@link Param} if specified. When {@link Param} is not specified,
   * the parameter index is used. Note that this index could be different from the actual index
   * when the method has special parameters (i.e. {@link RowBounds} or {@link ResultHandler}).
   * </p>
   * <ul>
   * <li>aMethod(@Param("M") int a, @Param("N") int b) -&gt; {{0, "M"}, {1, "N"}}</li>
   * <li>aMethod(int a, int b) -&gt; {{0, "0"}, {1, "1"}}</li>
   * <li>aMethod(int a, RowBounds rb, int b) -&gt; {{0, "0"}, {2, "1"}}</li>
   * </ul>
   * 记录参数在参数列表中的位置索引与参数名称之间的对应关系,
   * key:参数在参数列表中的索引位置
   * value:参数名称,参数名称可以通过@Param注解指定
   * 如果没有指定@Parma注解,则使用参数索引作为其名称
   * 如果参数列表中包含RowBounds类型或ResultHandler类型的参数,
   * 则这两种类型的参数并不会被记录到name集合中
   * 这就会导致参数的索引与名称不一致
   */
  private final SortedMap<Integer, String> names;
  //记录对应的方法的参数列表中是否使用了@Param注解
  private boolean hasParamAnnotation;

  public ParamNameResolver(Configuration config, Method method) {
    //获取参数列表中每个参数的类型
    final Class<?>[] paramTypes = method.getParameterTypes();
    //获取参数列表上的注解
    final Annotation[][] paramAnnotations = method.getParameterAnnotations();
    //该集合用于记录参数索引与参数名称的对应关系
    final SortedMap<Integer, String> map = new TreeMap<>();
    int paramCount = paramAnnotations.length;
    // get names from @Param annotations
    //遍历方法所有参数
    for (int paramIndex = 0; paramIndex < paramCount; paramIndex++) {
      if (isSpecialParameter(paramTypes[paramIndex])) {
        // skip special parameters
        //如果参数是RowBounds类型或ResultHandler类型,跳过对该参数的分析
        continue;
      }
      String name = null;
      //遍历该参数对应的注解集合
      for (Annotation annotation : paramAnnotations[paramIndex]) {
        if (annotation instanceof Param) {
          //@Param注解出现过一次,就将hasParamAnnotation初始化为true
          hasParamAnnotation = true;
          //获取@Param注解指定的参数名称
          name = ((Param) annotation).value();
          break;
        }
      }
      if (name == null) {
        // @Param was not specified.
        //该参数没有对应的@Param注解,则根据配置决定是否使用参数的实际名称作为其名称
        if (config.isUseActualParamName()) {//默认为true
          //获取该参数的实际名称
          name = getActualParamName(method, paramIndex);
        }
        if (name == null) {
          // use the parameter index as the name ("0", "1", ...)
          // gcode issue #71
          //使用参数索引作为其名称
          name = String.valueOf(map.size());
        }
      }
      map.put(paramIndex, name);//记录到map中
    }
    //初始化names集合
    names = Collections.unmodifiableSortedMap(map);
  }
  //获取该参数的实际名称
  private String getActualParamName(Method method, int paramIndex) {
    return ParamNameUtil.getParamNames(method).get(paramIndex);
  }
  //过滤RowBounds和ResultHandler两种类型的参数
  private static boolean isSpecialParameter(Class<?> clazz) {
    return RowBounds.class.isAssignableFrom(clazz) || ResultHandler.class.isAssignableFrom(clazz);
  }

  /**
   * Returns parameter names referenced by SQL providers.
   */
  public String[] getNames() {
    return names.values().toArray(new String[0]);
  }

  /**
   * <p>
   * A single non-special parameter is returned without a name.
   * Multiple parameters are named using the naming rule.
   * In addition to the default names, this method also adds the generic names (param1, param2,
   * ...).
   * </p>
   * 接受用户传入的实参列表,并将实参与其对应的名称进行关联
   */
  public Object getNamedParams(Object[] args) {
    final int paramCount = names.size();
    if (args == null || paramCount == 0) {//无参数 返回null
      return null;
    } else if (!hasParamAnnotation && paramCount == 1) {//未使用@Param且只有一个参数
      return args[names.firstKey()];//返回第一个参数
    } else {//处理使用@Param注解指定了参数名称或有多个参数的情况
      final Map<String, Object> param = new ParamMap<>();
      int i = 0;
      for (Map.Entry<Integer, String> entry : names.entrySet()) {
        //将参数名和实参对应关系记录到param中
        param.put(entry.getValue(), args[entry.getKey()]);
        // add generic param names (param1, param2, ...)
        //为参数创建"param+索引"格式的默认参数名称,例如param1,param2
        //并添加到param中
        final String genericParamName = GENERIC_NAME_PREFIX + String.valueOf(i + 1);
        // ensure not to overwrite parameter named with @Param
        if (!names.containsValue(genericParamName)) {
          param.put(genericParamName, args[entry.getKey()]);
        }
        i++;
      }
      return param;
    }
  }
}
