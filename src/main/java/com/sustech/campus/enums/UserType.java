package com.sustech.campus.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum UserType {
    USER(0, "user"),
    ADMIN(1, "admin"),
    MUTED(2, "muted");
    @EnumValue
    private int typeId;
    private String typeName;

    UserType(int typeId, String typeName) {
        this.typeId = typeId;
        this.typeName = typeName;
    }
}