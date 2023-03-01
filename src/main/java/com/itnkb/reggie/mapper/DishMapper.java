package com.itnkb.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itnkb.reggie.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
