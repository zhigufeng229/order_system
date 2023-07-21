package com.order.chandler.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.order.chandler.entity.ShoppingCart;
import com.order.chandler.mapper.ShoppingCartMapper;
import com.order.chandler.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
