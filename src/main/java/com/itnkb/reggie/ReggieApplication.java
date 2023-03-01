package com.itnkb.reggie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @Slf4j --提供log 用于日志功能
 * @SpringBootApplication --定义SpringBoot启动类
 * @ServletComponentScan --开启过滤器等操作
 */
@Slf4j
@SpringBootApplication
@ServletComponentScan
@EnableTransactionManagement
public class ReggieApplication {
    public static void main(String[] args) {
        /**
         * 启动SpringBoot工程
         * TODO 错误一:SpringApplication.run(xx.class,args)中,xx为启动类名
         */
        SpringApplication.run(ReggieApplication.class,args);
        //由 @Slf4j 提供的日志功能,log.xx("")中,xx为日志的等级
        log.info("项目启动成功...");
    }
}
