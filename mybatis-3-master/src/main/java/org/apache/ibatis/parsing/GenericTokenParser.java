/**
 *    Copyright 2009-2017 the original author or authors.
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

/**
 * @author Clinton Begin
 * 通用的字占位符解析器
 */
public class GenericTokenParser {

  private final String openToken;//占位符的开始标记
  private final String closeToken;//占位符的结束标记
  private final TokenHandler handler;//按照一定的逻辑解析占位符
  //构造器
  public GenericTokenParser(String openToken, String closeToken, TokenHandler handler) {
    this.openToken = openToken;
    this.closeToken = closeToken;
    this.handler = handler;
  }

  /**
   * 找到指定的占位符
   * @param text
   * @return
   */
  public String parse(String text) {
    //校验字符串是否为空
    if (text == null || text.isEmpty()) {
      return "";
    }
    // search open token 查找开始标记
    int start = text.indexOf(openToken, 0);
    //检测start是否为-1
    if (start == -1) {
      return text;
    }
    char[] src = text.toCharArray();
    int offset = 0;
    //用来记录解析后的字符串
    final StringBuilder builder = new StringBuilder();
    //用来记录一个占位符的字面值
    StringBuilder expression = null;
    while (start > -1) {
      if (start > 0 && src[start - 1] == '\\') {//遇到转义的开始标记
        // this open token is escaped. remove the backslash and continue.
        //则直接将前面的字符串以及开始标记追加到builder中
        builder.append(src, offset, start - offset - 1).append(openToken);
        offset = start + openToken.length();
      } else {
        // found open token. let's search close token.
        //查找到开始标记且没有转义
        if (expression == null) {
          expression = new StringBuilder();
        } else {
          expression.setLength(0);
        }
        //将前面的字符串追加到builder中
        builder.append(src, offset, start - offset);
        //修改offset的位置
        offset = start + openToken.length();
        //从offset向后继续查找结束标记
        int end = text.indexOf(closeToken, offset);
        while (end > -1) {
          if (end > offset && src[end - 1] == '\\') {//遇到转义的结束标记
            // this close token is escaped. remove the backslash and continue.
            //直接把结束标记前面的字符串追加到expression再把结束标记追加进去
            expression.append(src, offset, end - offset - 1).append(closeToken);
            //修改offset的值
            offset = end + closeToken.length();
            //获取下一个结束标记位置
            end = text.indexOf(closeToken, offset);
          } else {
            //如果找到结束标记且为不转义 将开始标记和结束标记之间的字符串追加到expression中保存
            expression.append(src, offset, end - offset);
            //修改offset值
            offset = end + closeToken.length();
            break;
          }
        }
        //未找到结束标记
        if (end == -1) {
          // close token was not found.
          //把开始标记后面的全部追加到builder中
          builder.append(src, start, src.length - start);
          //修改offset
          offset = src.length;
        } else {
          //将占位符的字面值交给TokenHandle处理,并将处理结果追加到builder中保存
          builder.append(handler.handleToken(expression.toString()));
          //修改offset
          offset = end + closeToken.length();
        }
      }
      start = text.indexOf(openToken, offset);//移动start
    }
    //遍历完标记符以后 如果offset小于src的length
    if (offset < src.length) {
      //那么把offset后面的字符串追加到builder中
      builder.append(src, offset, src.length - offset);
    }
    return builder.toString();
  }
}
