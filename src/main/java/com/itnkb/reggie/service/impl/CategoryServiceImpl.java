package com.itnkb.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itnkb.reggie.common.CustomException;
import com.itnkb.reggie.entity.Category;
import com.itnkb.reggie.entity.Dish;
import com.itnkb.reggie.entity.Setmeal;
import com.itnkb.reggie.mapper.CategoryMapper;
import com.itnkb.reggie.service.CategoryService;
import com.itnkb.reggie.service.DishService;
import com.itnkb.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;
    /**
     * 根据id删除分类,删除之前需要进行判断:是否有已经关联的菜品和套餐
     * @param id
     */
    public void remove(Long id){
        //封装菜品查询条件
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int count1 = dishService.count(dishLambdaQueryWrapper);

        //封装套餐查询条件
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count2 = dishService.count(dishLambdaQueryWrapper);

        //查询当前分类是否关联了菜品,如果已经关联,抛出一个业务异常
        if(count1 > 0) {
            //已经关联菜品,抛出一个业务异常
            throw new CustomException("当前这个分类下已有关联菜品,不能删除");
        }

        //查询当前分类是否关联了套餐,如果已经关联,抛出一个业务异常
        if(count2 > 0){
            //已经关联套餐,抛出一个业务异常
            throw new CustomException("当前这个分类下已有关联套餐,不能删除");
        }

        //正常删除分类
        super.removeById(id);

    }
}
