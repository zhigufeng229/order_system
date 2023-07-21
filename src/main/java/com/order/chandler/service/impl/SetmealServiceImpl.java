package com.order.chandler.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.order.chandler.common.CustomException;
import com.order.chandler.dto.SetmealDto;
import com.order.chandler.entity.Setmeal;
import com.order.chandler.entity.SetmealDish;
import com.order.chandler.mapper.SetmealMapper;
import com.order.chandler.service.SetmealDishService;
import com.order.chandler.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 套餐Service
 */
@Service
@Transactional
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setmealDto
     */
    public void addWithDish(SetmealDto setmealDto){
        //保存套餐基本信息
        this.save(setmealDto);

        //获取套餐Id(保存套餐信息时，mp自动生成的id)
        Long setmealId = setmealDto.getId();

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealId);
            return item;
        }).collect(Collectors.toList());

        //保存套餐与菜品关联的信息
        setmealDishService.saveBatch(setmealDishes);

    }

    /**
     * 删除套餐，同时需要删除套餐和菜品的关联关系
     * @param ids
     */
    @Override
    public void deleteWithDish(List<Long> ids) {
        //条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //查询与菜品是否有关联
        queryWrapper.in(Setmeal::getId, ids);
        queryWrapper.eq(Setmeal::getStatus, 1);

        int count = this.count(queryWrapper);

        if(count > 0){
            //套餐在售中，不能删除
            throw new CustomException("套餐在售中，不能删除");
        }
        //可以删除，先删除套餐表中信息
        this.removeByIds(ids);

        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //删除关系表中的数据
        setmealDishLambdaQueryWrapper.in(SetmealDish::getSetmealId, ids);
        setmealDishService.remove(setmealDishLambdaQueryWrapper);
    }

    /**
     * 修改套餐
     * @param setmealDto
     */
    @Override
    public void updateWithDish(SetmealDto setmealDto) {
        //更新套餐基本信息
        this.updateById(setmealDto);

        //更细套餐与菜品的关联信息
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
       setmealDishes.stream().map((item)->{
           //设置套餐id
           item.setSetmealId(setmealDto.getId());
           return item;
       }).collect(Collectors.toList());

       setmealDishService.updateBatchById(setmealDishes);
    }
}
