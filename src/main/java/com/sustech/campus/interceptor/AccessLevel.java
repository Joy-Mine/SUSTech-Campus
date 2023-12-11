package com.sustech.campus.interceptor;

public enum AccessLevel {

    // 此处需与user表的role字段对应起来

    USER(1, "all"), // 普通用户
    ADMIN(3, "admin"); // 管理员

    int code;
    String msg;

    AccessLevel(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
