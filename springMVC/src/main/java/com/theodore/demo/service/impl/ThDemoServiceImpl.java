package com.theodore.demo.service.impl;

import com.theodore.anno.TheodoreService;
import com.theodore.demo.service.ThDemoService;

@TheodoreService
public class ThDemoServiceImpl implements ThDemoService {

    @Override
    public String get(String name) {
        System.out.printf("service中的name:" + name);
        return name;
    }
}
