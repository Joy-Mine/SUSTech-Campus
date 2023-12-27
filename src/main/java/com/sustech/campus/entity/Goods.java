package com.sustech.campus.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.math.BigDecimal;

@TableName("goods")
public class Goods {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String name;

    @TableField(value = "storeId")
    private Long storeId;

    private BigDecimal price;

    private Integer quantity;

    private Boolean hidden;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    @Override
    public String toString() {
        return "Goods{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", storeId=" + storeId +
                ", price=" + price +
                ", quantity=" + quantity +
                ", hidden=" + hidden +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Goods other)) {
            return false;
        }
        return this == other
                || this.id.equals(other.id)
                && this.name.equals(other.name)
                && this.storeId.equals(other.storeId)
                && this.price.compareTo(other.price) == 0
                && this.quantity.equals(other.quantity)
                && this.hidden.equals(other.hidden);
    }
}
