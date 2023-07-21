package com.order.chandler.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.order.chandler.common.CustomException;
import com.order.chandler.entity.Category;
import com.order.chandler.entity.Dish;
import com.order.chandler.entity.Setmeal;
import com.order.chandler.mapper.CategoryMapper;
import com.order.chandler.service.CategoryService;
import com.order.chandler.service.DishService;
import com.order.chandler.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;
    //删除菜品或套餐
    @Override
    public void remove(Long id) {
        //条件构造器
        LambdaQueryWrapper<Dish> dishQueryWrapper = new LambdaQueryWrapper<>();

        //添加查询条件，根据分类id进行查询
        dishQueryWrapper.eq(Dish::getCategoryId, id);
        int count1 = dishService.count(dishQueryWrapper);

        //如果count1 > 0，则表示当前分类关联了菜品，不能删除该分类
        if (count1 > 0){
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
        int count2 = setmealService.count(setmealLambdaQueryWrapper);

        //如果count2 > 0，则表示当前分类关联了套餐，不能删除该分类
        if(count2 > 0){
            throw new CustomException("当前分类下关联了套餐，不能删除");
        }
        //当前分类和菜品、套餐都没有关联，正常删除
        super.removeById(id);

    }
}
