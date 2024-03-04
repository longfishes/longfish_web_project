package com.longfish.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//请求处理类
@RestController
public class HelloController {

    @RequestMapping("/hello")
    public String hello(){
        System.out.println("hello world");
        return "我是你爹";
    }
}
