package com.order.chandler.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.order.chandler.common.R;
import com.order.chandler.dto.DishDto;
import com.order.chandler.entity.Category;
import com.order.chandler.entity.Dish;
import com.order.chandler.entity.DishFlavor;
import com.order.chandler.service.CategoryService;
import com.order.chandler.service.DishFlavorService;
import com.order.chandler.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 *  菜品管理
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 添加菜品
     * @return
     */
    @PostMapping
    public R<String> add( @RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功！");
    }

    /**
     * 菜品信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(Integer page, Integer pageSize, String name){

        //分页构造器
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();

        //添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name), Dish::getName, name);

        //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        //执行分页查询
        dishService.page(pageInfo, queryWrapper);

        //对象拷贝，忽略records属性值
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");

        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = records.stream().map((item)->{
            DishDto  dishDto = new DishDto();
            //将item的属性值复制到 disDto
            BeanUtils.copyProperties(item, dishDto);
            //获取菜品分类的Id值
            Long categoryId = item.getCategoryId();
            //获取菜品分类对象
            Category category = categoryService.getById(categoryId);
            if(category != null) {
                dishDto.setCategoryName(category.getName());
            }
            return dishDto;

        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);

    }

    /**
     * 通过菜品id，获取菜品信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        log.info("菜品id为：{}",id);
        DishDto dishDto = dishService.getByIdFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        log.info("获取到的修改数据{}", dishDto.toString());

        dishService.updateWithFlavor(dishDto);
        return R.success("修改成功!");
    }

    /**
     * 通过菜品id删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> deleteById(@RequestParam List<Long> ids){

        log.info("删除的id为：{}",ids.toString());
        dishService.deleteWithFlavor(ids);
        return R.success("删除成功");

    }

    /**
     * 根据菜品id修改菜品状态
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> status(@PathVariable Integer status, @RequestParam List<Long> ids){
        log.info("修改id = {}, 的状态为：{}",ids, status);
        List<Dish> dishList = dishService.listByIds(ids);
        dishList.stream().map((item)->{
            item.setStatus(status);
            return item;
        }).collect(Collectors.toList());

        //更改菜品售卖状态
        dishService.updateBatchById(dishList);

        return R.success("修改成功");
    }

    /**
     * 通过分类id，查询菜品
     * @param dish
     * @return
     */
//    @GetMapping("/list")
//    public R<List<Dish>> list(Dish dish){
//        //条件构造器
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
//        //确保菜品是在售的状态（ 1：在售  0： 停售）
//        queryWrapper.eq(Dish::getStatus, 1);
//        //添加排序条件
//        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//
//        List<Dish> list = dishService.list(queryWrapper);
//
//        return R.success(list);
//
//    }

    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        //确保菜品是在售的状态（ 1：在售  0： 停售）
        queryWrapper.eq(Dish::getStatus, 1);
        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

        //获取菜品中的口味信息
        List<DishDto> dishDtoList = list.stream().map((item)->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);

            //查询菜品对应的口味信息
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId, dishId);
            List<DishFlavor> dishFlavors = dishFlavorService.list(lambdaQueryWrapper);

            dishDto.setFlavors(dishFlavors);
            return dishDto;

        }).collect(Collectors.toList());

        return R.success(dishDtoList);

    }

}
