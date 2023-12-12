package com.sustech.campus.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum UserType {
    USER(0, "user"),
    ADMIN(1, "admin"),
    VISITOR(2,"visitor"),
    MUTED(3, "muted");
    @EnumValue
    private int typeId;
    private String typeName;

    UserType(int typeId, String typeName) {
        this.typeId = typeId;
        this.typeName = typeName;
    }

    public static UserType getTypeByName(String typeName) {
        for (UserType type : UserType.values())
            if (type.typeName.equalsIgnoreCase(typeName))
                return type;
        throw new IllegalArgumentException("No UserType with typeName " + typeName + " found");
    }
}