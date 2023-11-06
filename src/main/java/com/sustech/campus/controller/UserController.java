package com.sustech.campus.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @GetMapping("/user/{id}")
    public String getUserByID(@PathVariable int id){
        return "user: "+id;
    }
}
