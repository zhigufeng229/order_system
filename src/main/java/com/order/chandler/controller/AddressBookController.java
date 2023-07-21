package com.order.chandler.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.order.chandler.common.BaseContext;
import com.order.chandler.common.R;
import com.order.chandler.entity.AddressBook;
import com.order.chandler.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/addressBook")
@Slf4j
public class AddressBookController {

    @Autowired
    private AddressBookService addressService;

    /**
     * 新增地址
     * @param addressBook
     * @return
     */
    @PostMapping
    public R<String> add(@RequestBody AddressBook addressBook, HttpServletRequest request){
        log.info("地址信息为：{}",addressBook.toString());
//        //获取当前线程的用户id
//        Long userId = BaseContext.get();
//        log.info("userId" + userId);
        Long userId = (Long) request.getSession().getAttribute("user");
        log.info("userId1: " + userId);
        addressBook.setUserId(userId);

        addressService.save(addressBook);
        return R.success("新增地址成功");
    }

    /**
     * 获取当前用户的地址列表
     * @return
     */
    @GetMapping("/list")
    public R<List<AddressBook>> list(HttpServletRequest request){
        log.info("地址列表...");
        //获取用户id
        Long userId = (Long) request.getSession().getAttribute("user");

        //条件构造器
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(AddressBook::getUserId, userId);

        //根据userId 查询地址
        List<AddressBook> list = addressService.list(queryWrapper);

        return R.success(list);
    }


    /**
     * 设置默认地址
     * @param addressBook
     * @return
     */
    @PutMapping("/default")
    public R<AddressBook> defaultAddress(@RequestBody AddressBook addressBook, HttpServletRequest request){
        log.info("默认地址id：{}", addressBook.toString());

        //获取用户id
        Long userId = (Long) request.getSession().getAttribute("user");

        //将该用户下的所有地址都设置成非默认地址
        LambdaUpdateWrapper<AddressBook> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AddressBook::getUserId, userId);
        // 1：默认地址  0 ：不是默认地址
        updateWrapper.set(AddressBook::getIsDefault, 0);
        addressService.update(updateWrapper);

        //将当前地址设置成默认地址
         addressBook.setIsDefault(1);
         addressService.updateById(addressBook);

        return R.success(addressBook);
    }

    /**
     * 通过id查询地址
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<AddressBook> queryById(@PathVariable Long id){
        log.info("要修改的地址id为：{}", id);

        //获取地址信息
        AddressBook addressBook = addressService.getById(id);
        if(addressBook != null){
            return R.success(addressBook);
        }
        return R.error("获取地址信息失败");
    }

    /**
     * 修改地址
     * @param addressBook
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody AddressBook addressBook){
        log.info("修改的地址为：{}",addressBook.toString());
        addressService.updateById(addressBook);
        return R.success("修改成功");
    }

    /**
     * 删除地址
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long ids){
        log.info("删除的地址id为：{}",ids);

        addressService.removeById(ids);
        return R.success("删除地址成功");
    }

    /**
     * 获取默认地址
     * @param request
     * @return
     */
    @GetMapping("/default")
    public R<AddressBook> getDefault(HttpServletRequest request){
        log.info("获取默认地址...");
        //获取用户id
        Long userId = (Long) request.getSession().getAttribute("user");

        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, userId);
        queryWrapper.eq(AddressBook::getIsDefault, 1);

        AddressBook address = addressService.getOne(queryWrapper);
        return R.success(address);

    }

}
