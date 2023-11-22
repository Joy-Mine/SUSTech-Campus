package com.sustech.campus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;

@TableName("goods")
public class Goods {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String name, store;

    private BigDecimal price;

    private Integer quantity;

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

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
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

    @Override
    public String toString() {
        return "Goods{" +
                "id=" + id +
                ", name='" + name + "'" +
                ", store='" + store + "'" +
                ", price=" + price +
                ", quantity=" + quantity +
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
                && this.store.equals(other.store)
                && this.price.compareTo(other.price) == 0
                && this.quantity.equals(other.quantity);
    }
}
