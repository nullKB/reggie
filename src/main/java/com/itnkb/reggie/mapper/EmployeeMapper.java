package com.itnkb.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itnkb.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * TODO @Mapper --定义mapper类
 * TODO extends BaseMapper<xx>:通过继承BaseMapper<xx>类来实现基础的增删改查功能
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
