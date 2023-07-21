package com.order.chandler.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.order.chandler.entity.DishFlavor;
import com.order.chandler.mapper.DishFlavorMapper;
import com.order.chandler.service.DishFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl  extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
