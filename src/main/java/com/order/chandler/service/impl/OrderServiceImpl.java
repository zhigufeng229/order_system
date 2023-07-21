package com.order.chandler.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.order.chandler.common.BaseContext;
import com.order.chandler.common.CustomException;
import com.order.chandler.entity.*;
import com.order.chandler.mapper.OrdersMapper;
import com.order.chandler.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrderService {

    @Autowired
    private ShoppingCartService shoppingService;

    @Autowired
    private AddressBookService addressService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderDetailService detailService;

    /**
     * 提交订单
     * @param orders
     */
    @Override
    public void submit(Orders orders) {
        //获取当前用户id
        Long userId = BaseContext.get();

        //获取用户
        User user = userService.getById(userId);

        //查询用户的购物车信息
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId);
        List<ShoppingCart> shoppingCartList = shoppingService.list(queryWrapper);

        //判断购物车是否为空
        if (shoppingCartList == null || shoppingCartList.size() == 0){
            throw new CustomException("购物车为空，不能下单");
        }

        //查询用户地址
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressService.getById(addressBookId);

        if(addressBook == null){
            throw new CustomException("用户地址信息有误， 不能下单");
        }

        Long orderId = IdWorker.getId();   //生成订单号

        AtomicInteger amount = new AtomicInteger(0);

        List<OrderDetail> orderDetailList = shoppingCartList.stream().map((item)->{
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setImage(item.getImage());
            orderDetail.setName(item.getName());
            orderDetail.setAmount(item.getAmount());

            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());

            return orderDetail;
        }).collect(Collectors.toList());

        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setAmount(new BigDecimal(amount.get()));  //总金额
        orders.setStatus(2);
        orders.setUserId(userId);
        orders.setUserName(user.getName());
        orders.setNumber(String.valueOf(orderId));
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                           + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                           + (addressBook.getDistrictName() == null? "" : addressBook.getDistrictName())
                           + (addressBook.getDetail() == null ? "" : addressBook.getDetail())
        );
        //向订单表插入一条数据
        this.save(orders);

        //向订单明细表插入数据
        detailService.saveBatch(orderDetailList);

        //清空购物车数据
        shoppingService.remove(queryWrapper);


    }
}
