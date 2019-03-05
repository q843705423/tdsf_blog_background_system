package com.tiandisifang.controller;


import com.tiandisifang.service.UserService;
import com.tiandisifang.interceptor.UnCheck;
import com.tiandisifang.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
public class UserController {
    @Autowired
    UserService userService;
    @UnCheck//不过滤登陆
    
    @RequestMapping(method = POST,value = "login")
    public UserInfo loginController(String username, String password) {
        UserInfo y = userService.loginService(username,password);
        System.out.println("55555555555555555555555555"+ y);
        return userService.loginService(username,password);
    }
}
