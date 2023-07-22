package com.order.chandler.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.order.chandler.common.CustomException;
import com.order.chandler.dto.DishDto;
import com.order.chandler.entity.Dish;
import com.order.chandler.entity.DishFlavor;
import com.order.chandler.mapper.DishMapper;
import com.order.chandler.service.DishFlavorService;
import com.order.chandler.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜品Service
 */

@Service
@Transactional
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;



    @Autowired
    private RedisTemplate<String, Object> redisTemplate;



    /**
     * 新增菜品，同时保存对应的口味数据
     * @param dishDto
     */
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息到菜品表
        this.save(dishDto);

        //获得菜品id
        Long dishId = dishDto.getId();

        //菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item)->{
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());
        //保存菜品口味数据到菜品口味表
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdFlavor(Long id) {
        DishDto dishDto = new DishDto();
        //查询菜品基本信息， 从dish
        Dish dish = this.getById(id);

        //把dish的属性值复制到dishDto上
        BeanUtils.copyProperties(dish, dishDto);

        //查询当前菜品对应的口味信息 dish_flavor

        //条件构造器
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, id);

        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);
        return dishDto;
    }

    /**
     * 更新菜品信息和对应的口味信息
     * @param dishDto
     * @return
     */
    @Override
    public void updateWithFlavor(DishDto dishDto) {
        //修改菜品信息
       this.updateById(dishDto);

        //修改菜品对应的口味信息

        //清除当前菜品对应的口味数据  -- dish_flavor 表的delete操作
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(queryWrapper);

        //插入更新后的口味信息
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item)->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 删除菜品和其对应的口味信息
     * @param ids
     */
    @Override
    @Transactional
    public void deleteWithFlavor(List<Long> ids) {
        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Dish::getId,ids);
        //判断是否菜品处于在售状态
        queryWrapper.eq(Dish::getStatus, 1);
        int count = this.count(queryWrapper);

        //如果有菜品处于在售状态，则不能删除菜品
        if (count > 0){
            throw new CustomException("菜品处于正在售卖状态，不能删除");
        }
        //清除该菜品分类下的缓存信息
        List<Dish> dishList =this.listByIds(ids).stream().map((item)->{
            String key = "dish_" + item.getCategoryId() + "_1";
            redisTemplate.delete(key);
            return item;
        }).collect(Collectors.toList());

        //删除菜品信息
        this.removeByIds(ids);

        //删除菜品对应的口味信息  因为无法直接查询菜品的口味信息id，所以通过条件构造器的方式删除
        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorLambdaQueryWrapper.in(DishFlavor::getDishId, ids);
        dishFlavorService.remove(dishFlavorLambdaQueryWrapper);
    }
}
