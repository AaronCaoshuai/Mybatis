package com.aaron.generator.mapper;

import com.aaron.generator.domain.OrderDetail;
import java.util.List;

public interface OrderDetailMapper extends OrderDetailMapperExt{
    int deleteByPrimaryKey(Integer id);

    int insert(OrderDetail record);

    OrderDetail selectByPrimaryKey(Integer id);

    List<OrderDetail> selectAll();

    int updateByPrimaryKey(OrderDetail record);
}