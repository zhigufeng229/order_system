package com.order.chandler.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.order.chandler.common.R;
import com.order.chandler.entity.Category;
import com.order.chandler.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜品分类管理
 */
@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;


    /**
     * 新增菜品或套餐
     * @param category
     * @return
     */
    @PostMapping
    public R<String> add(@RequestBody Category category){
        log.info("新增菜品或套餐");
        boolean save = categoryService.save(category);
        if (save){
            return R.success("新增成功!");
        }
        return R.error("新增失败！");
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(Integer page, Integer pageSize){
        //分页构造器
        Page<Category> pageInfo = new Page<>();

        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();

        //添加排序条件，根据sort进行排序
        queryWrapper.orderByAsc(Category::getSort);

        categoryService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);

    }

    /**
     * 根据id删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping()
    public R<String> delete( Long ids){
        categoryService.remove(ids);
        return R.success("删除成功!");
    }

    /**
     * 修改分类
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info("修改分类信息：{}", category);
        categoryService.updateById(category);
        return R.success("修改成功");
    }

    /**
     * 获取菜品分类列表
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //添加条件
        queryWrapper.eq(category.getType() != null, Category::getType, category.getType());
        //添加排序条件
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list(queryWrapper);
        return R.success(list);
    }
}
