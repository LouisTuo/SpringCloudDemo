package com.crazy.hello.spring.cloud.web.admin.ribbon.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.ribbon.proxy.annotation.Hystrix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * TODO
 *
 * @author jaclon
 * @date 2019/6/12 0:37
 */
@Service
public class AdminService {
    @Autowired
    private RestTemplate restTemplate;

    @HystrixCommand(fallbackMethod = "loginError")
    public String sayHi(String message) {
        return restTemplate.getForObject("http://spring-cloud-service-admin/login?message=" + message, String.class);
    }

    public String loginError(String message) {
        return String.format("Hi, login failed,detail:%s", message);
    }
}
