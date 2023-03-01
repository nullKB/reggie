package com.itnkb.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itnkb.reggie.common.R;
import com.itnkb.reggie.dto.DishDto;
import com.itnkb.reggie.entity.Category;
import com.itnkb.reggie.entity.Dish;
import com.itnkb.reggie.entity.DishFlavor;
import com.itnkb.reggie.service.CategoryService;
import com.itnkb.reggie.service.DishFlavorService;
import com.itnkb.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    /**
     * 菜品分页查询
     * 前端返回的菜品信息中只包含分类ID而不包含分类名,所以需要对数据进行处理获得对应的分类名
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        //构造分页构造器对象
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        //构造条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(name != null,Dish::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        //执行分页查询
        dishService.page(pageInfo,queryWrapper);

        //进行对象拷贝
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");
        List<Dish> records = pageInfo.getRecords();

        //TODO 通过Dish数据查询出对应的分类名,并赋值给DishDto对象
        List<DishDto> list = records.stream().map((item) ->{
            DishDto dishDto = new DishDto();
            //对原Dish数据进行数据拷贝
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();//获取分类ID
            //查询对应的分类对象
            Category category = categoryService.getById(categoryId);
            //获取对应的分类名
            String categoryName = category.getName();
            //设置Dto对象的分类名
            dishDto.setCategoryName(categoryName);
            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);
        return R.success(dishDtoPage);

    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> getById(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);
        return R.success("修改菜品成功");
    }

    /**
     * 删除菜品信息 --可单个可多个
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> deleteOne(Long ids[]){
        log.info("删除菜品,ID为{}",ids);
        for(Long id : ids){
            dishService.remove(id);
        }
        return R.success("删除成功");
    }

    /**
     * 菜品停售
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> status(@PathVariable Integer status,Long[] ids) {
        //构造条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ids != null,Dish::getId,ids);
        List<Dish> list = dishService.list(queryWrapper);

        //依次修改售卖状态
        for(Dish dish : list){
            if (dish!=null) {
                dish.setStatus(status);
                dishService.updateById(dish);
            }
        }
        return R.success("修改成功");
    }

    /**
     * 根据条件查询对应的菜品
     * @param dish
     * @return
     */
//    @GetMapping("/list")
//    public R<List<Dish>> list(Dish dish){
//
//        //构造条件构造器,根据分类ID查询菜品
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper();
//        queryWrapper.in(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
//        //添加状态为启售的菜品
//        queryWrapper.eq(Dish::getStatus,1);
//        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//
//        //查询出对应分类ID的菜品
//        List<Dish> list = dishService.list(queryWrapper);
//
//        return R.success(list);
//    }

    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){

        //构造条件构造器,根据分类ID查询菜品
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.in(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
        //添加状态为启售的菜品
        queryWrapper.eq(Dish::getStatus,1);
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        //查询出对应分类ID的菜品
        List<Dish> list = dishService.list(queryWrapper);

        List<DishDto> dishDtoList = list.stream().map((item) ->{
            DishDto dishDto = new DishDto();
            //对原Dish数据进行数据拷贝
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();//获取分类ID
            //查询对应的分类对象
            Category category = categoryService.getById(categoryId);
            //获取对应的分类名
            String categoryName = category.getName();
            //设置Dto对象的分类名
            dishDto.setCategoryName(categoryName);

            //获取当前菜品的id
            Long dishId = item.getId();
            //封装当前菜品对应的口味
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorList);

            return dishDto;
        }).collect(Collectors.toList());

        return R.success(dishDtoList);
    }
}
