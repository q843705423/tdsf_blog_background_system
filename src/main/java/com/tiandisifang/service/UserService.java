package com.tiandisifang.service;


import com.tiandisifang.mapper.UserMapper;
import com.tiandisifang.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    UserMapper userMapper;
    public UserInfo loginService(String username,String password){
        UserInfo Y = userMapper.loginSearchMapper(username, password) ;
        Y.setUser_name(null);//清除用户名
//        Y.setUser_password(null);//清除密码内容
        return Y;
    }
}
