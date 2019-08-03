package com.atguigu.gulimall.oms.controller;

import com.atguigu.gulimall.oms.feign.WorldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @Autowired
WorldService worldService;
    @GetMapping("/hello")
    public String Hello() {
        String msg = "";
        msg = worldService.world();
        return "hello"+msg;
    }
}