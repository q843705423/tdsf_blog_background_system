package com.tiandisifang.mapper;


import com.tiandisifang.model.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper {
    //登陆验证
    @Select("Select * From user_info where user_name = #{username} and user_password = #{password}")//@SELECT * FROM user_info WHERE user_name = '1';
    public UserInfo loginSearchMapper(@Param("username") String username,@Param("password") String password);
}
