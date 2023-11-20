package com.sustech.campus.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum CommentStatus {
    WAITING(0, "waiting for approval"),
    APPROVED(1, "approved"),
    REJECTED(2, "rejected");

    @EnumValue
    private int typeId;

    private String typeName;

    CommentStatus(int typeId, String typeName) {
        this.typeId = typeId;
        this.typeName = typeName;
    }
}
