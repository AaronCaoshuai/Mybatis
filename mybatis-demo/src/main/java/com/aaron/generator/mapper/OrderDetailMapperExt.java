package com.aaron.generator.mapper;

import com.aaron.generator.domain.OrderDetail;

import java.util.List;

/**
 * 使用扩展类和扩展Mapper
 * 当数据库变动时候可以使用 Generator重新生成文件覆盖基础文件,同时保留以前的代码
 */
public interface OrderDetailMapperExt {
    List<OrderDetail> selectAllExt();
}
