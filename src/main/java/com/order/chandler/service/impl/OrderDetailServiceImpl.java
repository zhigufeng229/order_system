package com.order.chandler.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.order.chandler.entity.OrderDetail;
import com.order.chandler.mapper.OrderDetailMapper;
import com.order.chandler.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
