package com.order.chandler.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.order.chandler.common.R;
import com.order.chandler.entity.ShoppingCart;
import com.order.chandler.service.ShoppingCartService;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 购物车管理
 */
@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {

    @Autowired
     private ShoppingCartService shoppingService;


    /**
     * 购物车列表
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(HttpServletRequest request){
        //获取用户id
        Long userId = (Long) request.getSession().getAttribute("user");

        //条件构造器，查询userId 用户 的购物车列表
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(userId != null, ShoppingCart::getUserId, userId);
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> shoppingCartList = shoppingService.list(queryWrapper);

        return R.success(shoppingCartList);
    }

    /**
     * 根据id移除购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart, HttpServletRequest request){
        log.info("移除购物车的菜品或套餐是：{}", shoppingCart.toString());

        //获取用户id
        Long userId =(Long) request.getSession().getAttribute("user");

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId);
        //判断是菜品还是套餐
        Long dishId = shoppingCart.getDishId();
        if (dishId != null){
            queryWrapper.eq(ShoppingCart::getDishId, dishId);
        }else{
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }

        //查询对应的购物车信息
        ShoppingCart cart = shoppingService.getOne(queryWrapper);
        Integer number = cart.getNumber();

        //判断数量是否大于1，大于1数量减一， 否则移除购物车
        if(number > 1){
            cart.setNumber(number - 1);
            shoppingService.updateById(cart);
            return R.success(cart);
        }else{
            cart.setNumber(0);
            shoppingService.remove(queryWrapper);
            return R.success(cart);
        }



    }


    /**
     * 加入购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart, HttpServletRequest request){
        log.info("加入购物车的菜品为：{}", shoppingCart.toString());
        //获取用户id
        Long userId = (Long) request.getSession().getAttribute("user");

        shoppingCart.setUserId(userId);

        //条件构造器
        LambdaQueryWrapper<ShoppingCart> queryWrapper =  new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId);
        //判断加入购物车的是菜品还是套餐
        if(shoppingCart.getDishId() != null){
            //添加的是菜品
            queryWrapper.eq(ShoppingCart::getDishId, shoppingCart.getDishId());
            //确保口味一致
            queryWrapper.eq(shoppingCart.getDishFlavor() != null, ShoppingCart::getDishFlavor, shoppingCart.getDishFlavor());
        }else{
            //添加的是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }

        ShoppingCart shoppingCartOne= shoppingService.getOne(queryWrapper);

        if(shoppingCartOne != null){
            //如果已经存在，则在原来的基础上加一
            Integer number = shoppingCartOne.getNumber();
            shoppingCartOne.setNumber(number + 1);
            shoppingService.updateById(shoppingCartOne);

        }else{
            //不存在，直接添加购物车
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingService.save(shoppingCart);
            shoppingCartOne = shoppingCart;
        }
        return R.success(shoppingCartOne);
    }

    /**
     * 清空购物车
     * @param request
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean(HttpServletRequest request){
        //获取用户id
        Long userId = (Long) request.getSession().getAttribute("user");

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId);

        shoppingService.remove(queryWrapper);
        return R.success("清空购物车成功");
    }
}
