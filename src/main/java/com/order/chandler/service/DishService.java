package com.order.chandler.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.order.chandler.dto.DishDto;
import com.order.chandler.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {

    public void saveWithFlavor(DishDto dishDto);

    //根据id查询菜品信息和对应的口味信息
    public DishDto getByIdFlavor(Long id);

    //更新菜品信息和对应的口味信息
    public void updateWithFlavor(DishDto dishDto);

    //删除菜品和其对应的口味信息
    public void deleteWithFlavor(List<Long> ids);
}
