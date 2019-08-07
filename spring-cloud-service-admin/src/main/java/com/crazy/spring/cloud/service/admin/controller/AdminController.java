package com.crazy.spring.cloud.service.admin.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO
 *
 * @author jaclon
 * @date 2019/6/11 0:24
 */
@RestController
public class AdminController {

    @Value("${server.port}")
    private String port;

    @RequestMapping(value = "login", method = RequestMethod.GET)
    public String login(@RequestParam(value = "message") String message) {
        return String.format("is doing logging message:%s ,from port:%s", message, port);
    }
}
