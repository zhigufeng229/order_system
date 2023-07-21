package com.order.chandler.dto;


import com.order.chandler.entity.Dish;
import com.order.chandler.entity.DishFlavor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据传输对象，用于保存新添加的菜品信息
 */
@Data
public class DishDto  extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();
    private String categoryName;
    private Integer copies;
}
