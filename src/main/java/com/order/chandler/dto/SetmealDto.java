package com.order.chandler.dto;

import com.order.chandler.entity.Setmeal;
import com.order.chandler.entity.SetmealDish;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {
    private List<SetmealDish> setmealDishes = new ArrayList<>();
    private String categoryName;
    private String setmealName;
}
