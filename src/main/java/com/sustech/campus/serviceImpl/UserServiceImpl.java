package com.sustech.campus.serviceImpl;

import com.sustech.campus.entity.User;
import com.sustech.campus.enums.UserType;
import com.sustech.campus.mapper.UserMapper;
import com.sustech.campus.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public User getUserByName(String username) {
        return this.userMapper.selectById(username);
    }

    @Override
    public boolean userExists(String username) {
        return this.userMapper.selectById(username) != null;
    }

    @Override
    public boolean registerUser(String username, String password, UserType type) {
        if (this.userExists(username)) {
            return false;
        }
        User user = new User();
        user.setName(username);
        user.setPassword(password);
        user.setType(type);
        user.setToken("deadbeef");
        this.userMapper.insert(user);
        return true;
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
}
