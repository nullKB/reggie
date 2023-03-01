package com.itnkb.reggie.dto;

import com.itnkb.reggie.entity.Setmeal;
import com.itnkb.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
