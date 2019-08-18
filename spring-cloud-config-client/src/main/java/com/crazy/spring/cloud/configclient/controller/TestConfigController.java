package com.crazy.spring.cloud.configclient.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试配置中心客户端
 *
 * @author jaclon
 * @date 2019/8/11 15:34
 */
@RestController
public class TestConfigController {

    @Value("${foo}")
    private String foo;

    @RequestMapping(value = "/getConfigs", method = RequestMethod.GET)
    public String login() {
        return foo;
    }
}
