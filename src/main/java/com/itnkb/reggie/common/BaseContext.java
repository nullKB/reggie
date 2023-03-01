package com.itnkb.reggie.common;

/**
 * 基于ThreadLocal封装工具类:用户保存和获取当前登录用户id
 * TODO 在修改和添加功能中,LoginCheckFilter,EmployController和MyMetaObjecthandler共用一个线程,所以通过线程的局部属性ThreadLocal进行对公共字段的设置和获取
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal =new ThreadLocal<>();

    /**
     * 设置值
     * @param id
     */
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    /**
     * 获取值
     * @return
     */
    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
