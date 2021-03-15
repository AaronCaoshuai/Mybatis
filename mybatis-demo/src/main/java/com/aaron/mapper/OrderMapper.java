package com.aaron.mapper;

import com.aaron.domain.Order;

import java.util.List;

/**
 * orderMapper对象
 */
public interface OrderMapper {
    /**
     * 根据订单id查询订单和用户信息
     * @return
     * @throws Exception
     */
    Order selectUserByOrderId(Integer orderId) throws Exception;

    /**
     * 批量插入订单信息 对于批量插入不能使用 @Param注解
     * @throws Exception
     */
    void batchInsertOrdres(List<Order> orders) throws Exception;
}
