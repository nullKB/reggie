package com.itnkb.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itnkb.reggie.dto.DishDto;
import com.itnkb.reggie.entity.Dish;
import com.itnkb.reggie.entity.DishFlavor;
import com.itnkb.reggie.mapper.DishMapper;
import com.itnkb.reggie.service.DishFlavorService;
import com.itnkb.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品.同时保存口味数据
     * @param dishDto
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品基本信息
        this.save(dishDto);

        Long id = dishDto.getId();

        //获取口味信息
        List<DishFlavor> flavors = dishDto.getFlavors();

        //给每个口味信息赋予对应菜品ID
        flavors = flavors.stream().map((item) ->{
            item.setDishId(id);
            return item;
        }).collect(Collectors.toList());

        //批量保存口味信息
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品基本信息
        Dish dish = this.getById(id);

        //查询菜品口味信息
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);

        //拷贝信息到Dto对象
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        dishDto.setFlavors(flavors);

        return dishDto;
    }

    /**
     * 修改菜品.同时修改口味数据
     * @param dishDto
     */
    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //修改dish表基本信息
        this.updateById(dishDto);

        //清理当前菜品对应口味数据--dish_flavor的delete操作
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);


        //添加当前菜品对应口味数据--dish_flavor的insert操作
        //获取口味信息
        List<DishFlavor> flavors = dishDto.getFlavors();
        Long id = dishDto.getId();
        //给每个口味信息赋予对应菜品ID
        flavors = flavors.stream().map((item) ->{
            item.setDishId(id);
            return item;
        }).collect(Collectors.toList());
        //批量保存口味信息
        dishFlavorService.saveBatch(flavors);
    }

    @Override
    @Transactional
    public void remove(Long ids) {
        //删除菜品信息
        this.removeById(ids);

        //构建菜品相关口味条件构造器
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,ids);

        //删除菜品相关口味信息
        dishFlavorService.remove(queryWrapper);
    }
}


