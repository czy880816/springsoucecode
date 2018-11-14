package com.enjoy.geekq.service.impl;

import com.enjoy.geekq.annotation.EnjoyService;
import com.enjoy.geekq.service.GeekqService;

@EnjoyService("GeekqServiceImpl")
public class GeekqServiceImpl implements GeekqService {
    @Override
    public String query(String name, String age) {
        return "name ="+name+"age="+age;
    }
}
