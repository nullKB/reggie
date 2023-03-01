package com.itnkb.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itnkb.reggie.entity.ShoppingCart;
import com.itnkb.reggie.mapper.ShoppingCartMapper;
import com.itnkb.reggie.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
