package com.sustech.campus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sustech.campus.enums.UserType;

@TableName("user")
public class User {
    @TableId(type = IdType.INPUT)
    private String name;
    private String password;
    private UserType type;
    private String token;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserType getType() {
        return type;
    }

    public void setType(UserType type) {
        this.type = type;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof User other)) {
            return false;
        }
        return this == other
                || this.name.equals(other.name)
                && this.password.equals(other.password)
                && this.type == other.type
                && this.token.equals(other.token);
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + this.name + "'" +
                ", password='" + this.password + "'" +
                ", type='" + this.type + "'" +
                ", token='" + this.token + "'" +
                '}';
    }
}
