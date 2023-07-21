package com.order.chandler.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 自动填充功能
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    //插入时自动填充
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("start insert fill ....");
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("createUser", BaseContext.get());
        metaObject.setValue("updateUser", BaseContext.get());
        Long id = Thread.currentThread().getId();
        log.info("线程id:{}",id);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("start update fill ....");
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser", BaseContext.get());
        Long id = Thread.currentThread().getId();
        log.info("线程id:{}",id);
    }
}
