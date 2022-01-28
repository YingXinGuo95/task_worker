package com.guoyx.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class TestController extends BaseController{

    @RequestMapping("/")
    public String helloWorld() {
        return "hello";
    }

}
