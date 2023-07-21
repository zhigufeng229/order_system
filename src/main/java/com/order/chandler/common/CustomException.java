package com.order.chandler.common;

/**
 * 自定义业务异常处理类，处理移除菜品分类产生的异常
 */
public class CustomException extends RuntimeException{
    public CustomException(String msg){
        super(msg);
    }
}
