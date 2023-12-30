package com.sustech.campus.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sustech.campus.entity.User;
import com.sustech.campus.enums.UserType;
import com.sustech.campus.mapper.UserMapper;
import com.sustech.campus.service.UserService;
import com.sustech.campus.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public User getUserByName(String username) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("name", username);
        return this.userMapper.selectOne(wrapper);
    }

    @Override
    public User getUserByToken(String token) {
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        queryWrapper.eq("token", token);
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public User getUserById(Long userId) {
        return this.userMapper.selectById(userId);
    }

    @Override
    public UserType getUserType(Long userId) {
        User user = this.getUserById(userId);
        if (user == null) {
            return null;
        }
        return user.getType();
    }

    @Override
    public boolean userExists(String username) {
        return this.getUserByName(username) != null;
    }

    @Override
    public boolean userExists(Long userId) {
        return this.getUserById(userId) != null;
    }

    @Override
    public boolean changeUserName(Long userId, String newName) {
        User user = this.getUserById(userId);
        if (user == null || this.userExists(newName)) {
            return false;
        }
        user.setName(newName);
        this.userMapper.updateById(user);
        return true;
    }

    @Override
    public User registerUser(String username, String password, UserType type) {
        if (this.userExists(username)) {
            return null;
        }
        User user = new User();
        user.setName(username);
        user.setPassword(password);
        user.setType(type);
        user.setToken(Utils.generateToken(null));
        this.userMapper.insert(user);
        return user;
    }

    @Override
    public boolean changeUserType(Long userId, UserType newType) {
        User user = this.getUserById(userId);
        if (user == null) {
            return false;
        }
        user.setType(newType);
        this.userMapper.updateById(user);
        return true;
    }

    @Override
    public boolean deleteUser(Long userId) {
        if (!this.userExists(userId)) {
            return false;
        }
        this.userMapper.deleteById(userId);
        return true;
    }

    @Override
    public boolean checkToken(Long userId, String token) {
        User user = this.getUserById(userId);
        return user != null && user.getToken().equals(token);
    }

    @Override
    public String changeToken(Long userId) {
        User user = this.getUserById(userId);
        if (user == null) {
            return null;
        }
        user.setToken(Utils.generateToken(user.getToken()));
        this.userMapper.updateById(user);
        return user.getToken();
    }

    @Override
    public List<User> getAllUsers(){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        return userMapper.selectList(queryWrapper);
    }

    @Override
    public boolean muteUser(String username) {
        User user = this.getUserByName(username);
        if (user == null) {
            return false;
        }
        user.setType(UserType.MUTED);
        this.userMapper.updateById(user);
        return true;
    }

    @Override
    public boolean unmuteUser(String username) {
        User user = this.getUserByName(username);
        if (user == null) {
            return false;
        }
        user.setType(UserType.USER);
        this.userMapper.updateById(user);
        return true;
    }
}
