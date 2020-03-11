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
package org.apache.ibatis.scripting.xmltags;

import java.util.Map;

import org.apache.ibatis.builder.SqlSourceBuilder;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.session.Configuration;

/**
 * @author Clinton Begin
 */
public class DynamicSqlSource implements SqlSource {

	private final Configuration configuration;
	private final SqlNode rootSqlNode;

	public DynamicSqlSource(Configuration configuration, SqlNode rootSqlNode) {
		this.configuration = configuration;
		this.rootSqlNode = rootSqlNode;
	}

	@Override
	public BoundSql getBoundSql(Object parameterObject) {
		DynamicContext context = new DynamicContext(configuration, parameterObject);
		// 此处会处理
		rootSqlNode.apply(context);
		// 创建SQL信息解析器
		SqlSourceBuilder sqlSourceParser = new SqlSourceBuilder(configuration);
		// 获取入参类型
		Class<?> parameterType = parameterObject == null ? Object.class : parameterObject.getClass();
		// 执行解析：将带有#{}的SQL语句进行解析，然后封装到StaticSqlSource中
		SqlSource sqlSource = sqlSourceParser.parse(context.getSql(), parameterType, context.getBindings());
		// 将解析后的SQL语句还有入参绑定到一起（封装到一个对象中，此时还没有将参数替换到SQL占位符?）
		BoundSql boundSql = sqlSource.getBoundSql(parameterObject);
		for (Map.Entry<String, Object> entry : context.getBindings().entrySet()) {
			boundSql.setAdditionalParameter(entry.getKey(), entry.getValue());
		}
		return boundSql;
	}

}
