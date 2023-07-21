package com.order.chandler.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.order.chandler.entity.Orders;

public interface OrderService extends IService<Orders> {

    /**
     * 提交订单
     * @param orders
     */
    public void submit(Orders orders);
}
