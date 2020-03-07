package com.aaron.mybatisdemo.mapper;

import com.aaron.mybatisdemo.domain.Order;

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
}
