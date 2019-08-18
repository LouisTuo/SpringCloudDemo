package com.crazy.hello.spring.cloud.web.admin.feign;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * TODO
 *
 * @author jaclon
 * @date 2019/8/5 23:48
 */
@SpringBootApplication
// 开启Feign功能
@EnableFeignClients
// 开启发现Eureka
@EnableDiscoveryClient
// 开启熔断器的监控仪表盘
@EnableHystrixDashboard
public class WebAdminFeignApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebAdminFeignApplication.class, args);
    }
}
