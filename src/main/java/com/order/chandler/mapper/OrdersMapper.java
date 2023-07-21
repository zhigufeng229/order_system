package com.order.chandler.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.order.chandler.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {
}
