package com.itnkb.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itnkb.reggie.entity.OrderDetail;
import com.itnkb.reggie.mapper.OrderDetailMapper;
import com.itnkb.reggie.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
