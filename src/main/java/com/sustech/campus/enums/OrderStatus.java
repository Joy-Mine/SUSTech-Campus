package com.sustech.campus.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum OrderStatus {
    WAITING_PAYMENT(0, "waiting payment"),
//    PAID(1, "paid"),
    FINISHED(2, "finished"),
    CANCELLED(3, "cancelled");
//    REFUNDED(4, "refunded");
    @EnumValue
    private int statusId;
    private String statusName;

    OrderStatus(int statusId, String statusName) {
        this.statusId = statusId;
        this.statusName = statusName;
    }
}
