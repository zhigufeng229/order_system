package com.order.chandler.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.order.chandler.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {
}