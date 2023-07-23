package com.order.chandler.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.order.chandler.common.R;
import com.order.chandler.dto.SetmealDto;
import com.order.chandler.entity.Category;
import com.order.chandler.entity.Setmeal;
import com.order.chandler.entity.SetmealDish;
import com.order.chandler.service.CategoryService;
import com.order.chandler.service.SetmealDishService;
import com.order.chandler.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 套餐管理
 */
@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @CacheEvict(value = "setmealCache", allEntries = true)
    @PostMapping
    public R<String> add(@RequestBody SetmealDto setmealDto){

        log.info("新增套餐信息：{}", setmealDto.toString());

        setmealService.addWithDish(setmealDto);
        return R.success("新增套餐成功");

    }

    @GetMapping("/page")
    public R<Page> page(Integer page, Integer pageSize, String name){

        //分页构造器
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();

        //条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();

        //按照套餐名字模糊查询
        queryWrapper.like(name != null, Setmeal::getName, name);

        //排序
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        Page<Setmeal> setmealPage = setmealService.page(pageInfo, queryWrapper);

        //拷贝，将setmealPage的属性值 复制到 setmealDtoPage 中
        BeanUtils.copyProperties(setmealPage, setmealDtoPage, "records");

        List<Setmeal> records = setmealPage.getRecords();
        List<SetmealDto> setmealDtoList = records.stream().map((item)->{
            SetmealDto setmealDto = new SetmealDto();
            //拷贝，将item的属性值 复制到 setmealDto 中
            BeanUtils.copyProperties(item, setmealDto);

            Category category = categoryService.getById(item.getCategoryId());

            if(category != null) {
                //设置套餐分类名
                setmealDto.setCategoryName(category.getName());
            }
            return setmealDto;

        }).collect(Collectors.toList());

        setmealDtoPage.setRecords(setmealDtoList);

        return R.success(setmealDtoPage);


    }

    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @CacheEvict(value = "setmealCache", allEntries = true)
    @DeleteMapping
    public R<String> delete( @RequestParam List<Long> ids){
        log.info("需要删除的套餐id：{}", ids.toString());
        setmealService.deleteWithDish(ids);
        return R.success("套餐删除成功");
    }

    /**
     * 修改套餐在售状态
     * @param ids
     * @param status
     * @return
     */
    @CacheEvict(value = "setmealCache", allEntries = true)
    @PostMapping("/status/{status}")
    public R<String> status(@RequestParam List<Long> ids, @PathVariable Integer status){
        //根据套餐id获取套餐信息
        List<Setmeal> setmealList = setmealService.listByIds(ids);
        setmealList.stream().map((item)->{
            item.setStatus(status);
            return item;
        }).collect(Collectors.toList());

        //更新套餐在售状态
        setmealService.updateBatchById(setmealList);

        return R.success("售卖状态更改成功");
    }


    /**
     * 获取套餐列表
     * @param setmeal
     * @return
     */
    @Cacheable(value = "setmealCache", key = "#setmeal.categoryId + '_' + #setmeal.status")
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        //条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = setmealService.list(queryWrapper);

        return R.success(list);

    }

    /**
     * 通过id获取套餐信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> getSetmeal(@PathVariable Long id){

        SetmealDto setmealDto = new SetmealDto();
        //获取套餐基本信息
        Setmeal setmeal = setmealService.getById(id);
        BeanUtils.copyProperties(setmeal,setmealDto);

        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(id != null, SetmealDish::getSetmealId, id);
        List<SetmealDish> list = setmealDishService.list(queryWrapper);


        setmealDto.setSetmealDishes(list);

        return R.success(setmealDto);
    }


    /**
     * 修改套餐
     * @param setmealDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){
        log.info("要修改的套餐信息：{}", setmealDto.toString());
        setmealService.updateWithDish(setmealDto);
        return R.success("修改成功");
    }
}
