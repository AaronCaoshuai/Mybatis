package com.aaron.generator.domain;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * 订单明细表
 * 
 * t_order_detail
 * 
 **/
@Data
public class OrderDetail implements Serializable {
    /**
     * 订单明细id
     * id
     */
    private Integer id;

    /**
     * 创建时间
     * create_time
     */
    private Date createTime;

    /**
     * 修改时间
     * update_time
     */
    private Date updateTime;

    /**
     * 版本号
     * version
     */
    private Integer version;

    /**
     * 创建人id
     * create_user_id
     */
    private Integer createUserId;

    /**
     * 修改人id
     * update_user_id
     */
    private Integer updateUserId;

    /**
     * 订单明细备注
     * remark
     */
    private String remark;

    /**
     * 订单id
     * order_id
     */
    private Integer orderId;

    private static final long serialVersionUID = 1L;
}