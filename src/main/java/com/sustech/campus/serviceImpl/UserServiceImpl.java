package com.sustech.campus.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sustech.campus.entity.User;
import com.sustech.campus.enums.UserType;
import com.sustech.campus.mapper.UserMapper;
import com.sustech.campus.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    private String generateToken(String oldToken) {
        int length = 32;
        Random random = new Random();
        StringBuilder builder;
        do {
            builder = new StringBuilder();
            for (int i = 0; i < length; ++i) {
                builder.append((char) random.nextInt(33, 127));
            }
        } while (builder.toString().equals(oldToken));
        return builder.toString();
    }

    @Autowired
    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public User getUserByName(String username) {
        return this.userMapper.selectById(username);
    }

    @Override
    public User getUserByToken(String token) {
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        queryWrapper.eq("token", token);
        return this.userMapper.selectOne(queryWrapper);
    }

    @Override
    public UserType getUserType(String username) {
        User user = this.getUserByName(username);
        if (user == null) {
            return null;
        }
        return user.getType();
    }

    @Override
    public boolean userExists(String username) {
        return this.userMapper.selectById(username) != null;
    }

    @Override
    public String registerUser(String username, String password, UserType type) {
        if (this.userExists(username)) {
            return null;
        }
        User user = new User();
        user.setName(username);
        user.setPassword(password);
        user.setType(type);
        user.setToken(this.generateToken(null));
        this.userMapper.insert(user);
        return user.getToken();
    }

    @Override
    public boolean changeUserType(String username, UserType newType) {
        User user = this.getUserByName(username);
        if (user == null) {
            return false;
        }
        user.setType(newType);
        this.userMapper.updateById(user);
        return true;
    }

    @Override
    public boolean deleteUser(String username) {
        if (!this.userExists(username)) {
            return false;
        }
        this.userMapper.deleteById(username);
        return true;
    }

    @Override
    public boolean checkToken(String username, String token) {
        User user = this.getUserByName(username);
        return user != null && user.getToken().equals(token);
    }

    @Override
    public String changeToken(String username) {
        User user = this.getUserByName(username);
        if (user == null) {
            return null;
        }
        user.setToken(this.generateToken(user.getToken()));
        this.userMapper.updateById(user);
        return user.getToken();
    }
}
