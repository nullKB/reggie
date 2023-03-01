package com.itnkb.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itnkb.reggie.common.R;
import com.itnkb.reggie.entity.User;
import com.itnkb.reggie.service.UserService;
import com.itnkb.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 发送手机短信验证码
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user,HttpSession httpSession){
        //获取手机号
        String phone = user.getPhone();
        if(StringUtils.isNotEmpty(phone)){
            //生成随机的四位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code = {}",code);

            //调用阿里云提供的短信服务API完成发送短信
            //SMSUtils.sendMessage("阿里云短信测试","SMS_154950909",phone,code);

            //将生成的验证码保存在Session中
            httpSession.setAttribute(phone,code);
            return R.success("验证码发送成功");
        }
        return R.error("验证码发送失败");
    }

    /**
     * 移动端用户登录
     * @param map
     * @param httpSession
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession httpSession){
        log.info(map.toString());

        //获取手机号
        String phone = map.get("phone").toString();
        //获取用户输入的验证码
        String code = map.get("code").toString();
        //从Session中获取保存的验证码
        String trueCode = httpSession.getAttribute(phone).toString();

        //进行验证码的比对(页面提交的验证码和Session中保存的验证码)
        if(code.equals(trueCode)){
            //如果比对成功，说明登入成功
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            User one = userService.getOne(queryWrapper);
            if(one == null){
                //判断当前手机号是否为新用户,如果是新用户就自动完成注册
                one = new User();
                one.setPhone(phone);
                one.setStatus(1);
                userService.save(one);
            }
            httpSession.setAttribute("user",one.getId());
            return R.success(one);
        }
        return R.error("登录失败");
    }
}
