package com.itnkb.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itnkb.reggie.entity.Category;

public interface CategoryService extends IService<Category> {
    public void remove(Long id);
}
