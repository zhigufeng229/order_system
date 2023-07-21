package com.order.chandler.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.order.chandler.dto.SetmealDto;
import com.order.chandler.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setmealDto
     */
    public void addWithDish(SetmealDto setmealDto);

    /**
     * 删除套餐，同时需要删除套餐和菜品的关联关系
     * @param ids
     */
    public void deleteWithDish(List<Long> ids);

    /**
     * 修改套餐
     * @param setmealDto
     */
    public void updateWithDish(SetmealDto setmealDto);
}
