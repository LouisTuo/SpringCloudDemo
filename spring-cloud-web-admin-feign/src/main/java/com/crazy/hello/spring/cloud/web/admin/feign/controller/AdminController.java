package com.crazy.hello.spring.cloud.web.admin.feign.controller;

import com.crazy.hello.spring.cloud.web.admin.feign.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO
 *
 * @author jaclon
 * @date 2019/8/6 0:07
 */
@RestController
public class AdminController {

  @Autowired private AdminService adminService;

  @RequestMapping(value = "login", method = RequestMethod.GET)
  public String login(@RequestParam String message) {
    return adminService.sayHi(message);
  }
}
