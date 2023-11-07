package com.sustech.campus.controller;

import com.sustech.campus.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @Autowired
    private UserMapper userMapper;

    @GetMapping("/user/{id}")
    public String getUserByID(@PathVariable int id){
        return userMapper.selectById(id).toString();
//        return "user: "+id;
    }
}
