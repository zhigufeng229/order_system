package com.order.chandler.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.order.chandler.entity.Category;

public interface CategoryService extends IService<Category> {

    //删除菜品或套餐
    public void remove(Long id);
}
