package com.order.chandler.common;

/**
 * 基于ThreadLocal封装的工具类，用于用户保存和获取当前登录用户的id
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static  void set(Long id){
        threadLocal.set(id);
    }
    public static Long get(){
        return threadLocal.get();
    }
}
