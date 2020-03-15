package com.theodore.demo.controller;

import com.theodore.anno.Security;
import com.theodore.anno.TheodoreAutowire;
import com.theodore.anno.TheodoreController;
import com.theodore.anno.TheodoreRequestMapping;
import com.theodore.demo.service.ThDemoService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@TheodoreController
@TheodoreRequestMapping("/demo")
@Security({"lisi", "zhangsan"})
public class ThController {

    @TheodoreAutowire
    private ThDemoService thDemoService;

    @Security("zhangsan")
    @TheodoreRequestMapping("/handler01")
    public String handler01(HttpServletRequest request, HttpServletResponse response,String username){
        return thDemoService.get(username);
    }


    @Security("lisi")
    @TheodoreRequestMapping("/handler02")
    public String handler02(HttpServletRequest request, HttpServletResponse response,String username){
        return thDemoService.get(username);
    }
}
