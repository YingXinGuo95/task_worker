package com.guoyx.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController extends BaseController{

    @RequestMapping("/")
    public String helloWorld() {
        return "hello";
    }

    @RequestMapping("/filePanel")
    public String filePanel() {
        return "filePanel";
    }

}
