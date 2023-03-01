package com.itnkb.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itnkb.reggie.dto.SetmealDto;
import com.itnkb.reggie.entity.Setmeal;

public interface SetmealService extends IService<Setmeal> {

    /**
     * 新增套餐，同时保存套餐和菜品的关联
     * @param setmealDto
     */
    public void saveWithDish(SetmealDto setmealDto);

    public void remove(Long id);
}
