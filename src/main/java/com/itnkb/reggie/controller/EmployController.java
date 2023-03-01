package com.itnkb.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itnkb.reggie.common.R;
import com.itnkb.reggie.entity.Employee;
import com.itnkb.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;


@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     * TODO HttpServletRequest类:将员工ID传入session中,随取随用
     * TODO @RequestBody --将前端传来的JSON数据封装为所需类型
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){

        //1.将页面提交的密码password进行md5加密处理
        //TODO DigestUtils.md5DigestAsHex(password.getBytes()):对密码进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2.根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername()); //TODO 封装查询条件
        final Employee emp = employeeService.getOne(queryWrapper);  //查询数据库并返回对象

        //3.如果没有查询到则返回登录失败结果
        if(emp == null){
            R.error("登录失败!");
        }

        //4.密码对比,如果不一样返回登录失败结果
        if(!emp.getPassword().equals(employee.getPassword())){
            R.error("登录失败!");
        }

        //5.查看员工状态,如果为已禁用状态,则返回员工已禁用结果
        if(emp.getStatus() == 0){
            R.error("该账号已禁用!");
        }

        //6.登录成功,将员工id存入session并返回登录结果
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }

    /**
     * 员工退出
     * TODO 直接通过对于session中保存的ID直接清除,通过将code设为1给前端页面传输信号,将页面跳转到登陆页面
     * TODO HttpServletRequest类:清除员工ID
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        request.removeAttribute("employee");
        return R.success("推出成功");
    }

    /**
     * 新增员工
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        log.info("新增员工,员工信息:{}",employee.toString());

        //设置初始密码,需要进行md5加密处理
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        //设置创建时间和更新时间
        //employee.setCreateTime(LocalDateTime.now());
        //employee.setUpdateTime(LocalDateTime.now());

        //设置创建者和更新者
        //Long empId = (Long) request.getSession().getAttribute("employee");
        //employee.setCreateUser(empId);
        //employee.setUpdateUser(empId);

        //通过service保存信息
        employeeService.save(employee);
        return R.success("新增员工成功");
    }

    /**
     * 员工分页查询
     * Page:分页对象,其中的records属性为对数据项的封装
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        log.info("page = {},pageSize = {},name = {}",page,pageSize,name);

        //1.构造分页构造器
        Page pageInfo = new Page(page,pageSize);

        //2.构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        /**
         * 添加过滤条件
         * queryWrapper.like(xx,yy,zz):xx为判断是否需要进行过滤的条件,yy为过滤的条件,zz为过滤条件的值
         */
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        //3.执行查询
        employeeService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 根据ID修改员工信息
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        log.info(employee.toString());

        //1.获取管理员ID并且封装修改者和修改时间
        Long empId = (Long) request.getSession().getAttribute("employee");
        employee.setUpdateUser(empId);
        employee.setUpdateTime(LocalDateTime.now());

        //2.通过服务层完成对数据的修改
        employeeService.updateById(employee);

        return R.success("员工信息修改成功");
    }

    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("根据id查询员工信息");

        //1.根据id查询并封装对象
        Employee employee = employeeService.getById(id);

        if(employee != null){
            return R.success(employee);
        }

        return R.error("没有查询到员工信息");
    }
}
