package com.crazy.spring.cloud.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * 配置中心
 *
 * @author jaclon
 * @date 2019/8/10 17:36
 */
@SpringBootApplication
@EnableEurekaClient
// 开启SpirngCloudConfig 配置服务器功能
@EnableConfigServer
public class ConfigApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConfigApplication.class, args);
    }
}
