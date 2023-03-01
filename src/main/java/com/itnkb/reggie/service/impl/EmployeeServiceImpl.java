package com.itnkb.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itnkb.reggie.entity.Employee;
import com.itnkb.reggie.mapper.EmployeeMapper;
import com.itnkb.reggie.service.EmployeeService;
import org.springframework.stereotype.Service;

/**
 * TODO @Service --定义service层实现类
 * TODO extends ServiceImpl<xx, yy>:xx为实现类Mapper的类,yy为实现的实体类
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
