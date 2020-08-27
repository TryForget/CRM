package com.shsxt.crm.dao;

import com.shsxt.crm.base.BaseMapper;
import com.shsxt.crm.vo.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

public interface UserMapper extends BaseMapper<User, Integer> {

    // 通过用户名查询用户对象
    User queryUserByUserName(String userName);


    // 通过用户ID修改用户密码
    int updateUserPwd(@Param("userId") Integer userId, @Param("userPwd") String userPwd);

    // 查询所有的销售人员
    public List<Map<String, Object>> queryAllSales();


}