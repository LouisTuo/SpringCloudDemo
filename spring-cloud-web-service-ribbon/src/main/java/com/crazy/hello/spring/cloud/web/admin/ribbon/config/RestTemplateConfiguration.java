package com.crazy.hello.spring.cloud.web.admin.ribbon.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * TODO
 *
 * @author jaclon
 * @date 2019/6/12 0:35
 */
@Configuration
public class RestTemplateConfiguration {

    // @LoadBalanced 注解表明开启负载均衡功能
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
