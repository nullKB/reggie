package com.itnkb.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itnkb.reggie.dto.DishDto;
import com.itnkb.reggie.entity.Dish;

public interface DishService extends IService<Dish> {

    //新增菜品,同时插入菜品对应的口味数据,需要操作两张表:dish,dish_flavor
    public void saveWithFlavor(DishDto dishDto);

    //根据id查询菜品信息和对应的口味信息
    public DishDto getByIdWithFlavor(Long id);

    //修改菜品,同时修改菜品对应的口味数据,需要操作两张表:dish,dish_flavor
    public void updateWithFlavor(DishDto dishDto);

    //删除菜品同时删除与菜品相关的口味
    public void remove(Long ids);

}
