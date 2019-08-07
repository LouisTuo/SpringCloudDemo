package com.crazy.hello.spring.cloud.web.admin.feign.service.hystrix;

import com.crazy.hello.spring.cloud.web.admin.feign.service.AdminService;
import org.springframework.stereotype.Component;

/**
 * TODO
 *
 * @author jaclon
 * @date 2019/8/6 23:04
 */
@Component
public class AdminServiceHystrix implements AdminService {
    @Override
    public String sayHi(String message) {
        return String.format("Hi, login failed,detail:%s[From Feign Hystrix]", message);
    }
}
