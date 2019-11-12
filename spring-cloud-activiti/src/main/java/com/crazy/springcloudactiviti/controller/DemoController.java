package com.crazy.springcloudactiviti.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {
/*
    @Autowired
    RuntimeService runtimeService;

    @Autowired
    TaskService taskService;*/

    @GetMapping("/hello")
    public String hello() {

        return "";
    }
}
