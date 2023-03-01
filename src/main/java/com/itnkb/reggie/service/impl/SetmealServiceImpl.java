package com.itnkb.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itnkb.reggie.dto.SetmealDto;
import com.itnkb.reggie.entity.Setmeal;
import com.itnkb.reggie.entity.SetmealDish;
import com.itnkb.reggie.mapper.SetmealMapper;
import com.itnkb.reggie.service.DishService;
import com.itnkb.reggie.service.SetmealDishService;
import com.itnkb.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐，同时保存套餐和菜品的关联
     * @param setmealDto
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐的基本信息,操作setmeal表,执行insert操作
        this.save(setmealDto);

        //保存套餐和菜品的关联信息,操作setmeal_dish表,执行insert操作
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        //处理套餐-菜品关联表中ID
        setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    @Transactional
    public void remove(Long id) {
        //删除套餐 --操作setmeal表
        this.removeById(id);

        //删除套餐菜品关联表 --setmeal_dish表
        LambdaQueryWrapper<SetmealDish> queryWrapper =new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,id);
        setmealDishService.remove(queryWrapper);
    }
}
