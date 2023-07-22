package com.order.chandler;

import org.junit.jupiter.api.Test;

public class CommonControllerTest {

    @Test
    public void test(){
        String fileName = "test.jpg";
        String substring = fileName.substring(fileName.lastIndexOf("."));
        System.out.println(substring);
    }
    @Test
    public void test2(){
    }
}
