package com.crazy.hello.spring.cloud.web.admin.feign.service;

import com.crazy.hello.spring.cloud.web.admin.feign.service.hystrix.AdminServiceHystrix;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Feign
 */
// 这里有增加了熔断器

@FeignClient(value = "spring-cloud-service-admin",fallback = AdminServiceHystrix.class)
public interface AdminService {

    @RequestMapping(value = "login", method = RequestMethod.GET)
    public String sayHi(@RequestParam(value = "message") String message);
}
