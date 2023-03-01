package com.itnkb.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itnkb.reggie.common.R;
import com.itnkb.reggie.dto.SetmealDto;
import com.itnkb.reggie.entity.Category;
import com.itnkb.reggie.entity.Dish;
import com.itnkb.reggie.entity.Setmeal;
import com.itnkb.reggie.service.CategoryService;
import com.itnkb.reggie.service.SetmealDishService;
import com.itnkb.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 套餐管理
 */

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        log.info("套餐信息{}",setmealDto);
        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功");
    }

    /**
     * 套餐分页查询
     * @param pageSize
     * @param page
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page<SetmealDto>> page(int pageSize,int page,String name){
        //分页构造器
        Page<Setmeal> pageInfo = new Page<>(page,pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();

        //条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.like(name != null,Setmeal::getName,name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        //查询出结果
        setmealService.page(pageInfo,queryWrapper);

        //进行对象拷贝
        BeanUtils.copyProperties(pageInfo,setmealDtoPage,"records");
        List<Setmeal> records = pageInfo.getRecords();

        //TODO 通过Setmeal数据查询出对应的分类名,并赋值给SetmealDto对象
        List<SetmealDto> lists = records.stream().map((item)->{
            SetmealDto setmealDto = new SetmealDto();
            //拷贝Setmeal对象
            BeanUtils.copyProperties(item,setmealDto);
            //通过拷贝过来的分类ID获取对应分类
            Category category = categoryService.getById(setmealDto.getCategoryId());
            //获取对应的分类名
            String categoryName = category.getName();
            //设置Dto对象的分类名
            setmealDto.setCategoryName(categoryName);
            return setmealDto;
        }).collect(Collectors.toList());

        setmealDtoPage.setRecords(lists);
        return R.success(setmealDtoPage);
    }

    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long[] ids){
        for(Long id : ids){
            Setmeal setmeal = setmealService.getById(id);
            if(setmeal.getStatus() == 0){
                setmealService.remove(id);
            }else {
                return R.error("该套餐未停售");
            }
        }
        return R.success("删除成功");
    }

    /**
     * 套餐停售功能
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> status(@PathVariable Integer status,Long[] ids) {
        //构造条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ids != null,Setmeal::getId,ids);
        List<Setmeal> list = setmealService.list(queryWrapper);

        //依次修改售卖状态
        for(Setmeal setmeal : list){
            if (setmeal!=null) {
                setmeal.setStatus(status);
                setmealService.updateById(setmeal);
            }
        }
        return R.success("修改成功");
    }

    /**
     * 根据条价查询套餐数据
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null,Setmeal::getStatus,setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = setmealService.list(queryWrapper);
        return R.success(list);
    }
}
