package com.sustech.campus.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;

@TableName("order_item")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderItem {
    @TableField(value = "orderId")
    private Long orderId;

    @TableField(value = "goodsId")
    private Long goodsId;

    private Integer amount;

    private BigDecimal price;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "orderId=" + orderId +
                ", goodsId=" + goodsId +
                ", amount=" + amount +
                ", price=" + price +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof OrderItem other)) {
            return false;
        }
        return this == other
                || this.orderId.equals(other.orderId)
                && this.goodsId.equals(other.goodsId)
                && this.amount.equals(other.amount)
                && this.price.compareTo(other.price) == 0;
    }
}
