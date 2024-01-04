package com.sustech.campus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sustech.campus.enums.OrderStatus;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@TableName("`order`")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Order {
    @TableId(type = IdType.AUTO)
    private Long id;

    private OrderStatus status;

    private Long purchaser;

    private Long time;

    @TableField(exist = false)
    private List<OrderItem> items;

    @TableField(exist = false)
    private String purchaserName;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Long getPurchaser() {
        return purchaser;
    }

    public void setPurchaser(Long purchaser) {
        this.purchaser = purchaser;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public String getPurchaserName() {
        return purchaserName;
    }

    public void setPurchaserName(String purchaserName) {
        this.purchaserName = purchaserName;
    }

    public String getStringTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date(this.time));
    }

    public BigDecimal getTotalPrice() {
        BigDecimal totalPrice = BigDecimal.ZERO;
        if (items != null) {
            for (OrderItem item : items) {
                totalPrice = totalPrice.add(item.getPrice().multiply(BigDecimal.valueOf(item.getAmount())));
            }
        }
        return totalPrice;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", status=" + status +
                ", purchaser=" + purchaser +
                ", time=" + time +
                ", items=" + items +
                '}';
    }

    private boolean itemsEquals(Order other) {
        if (this.items == null || other.items == null) {
            return this.items == other.items;
        }
        return this.items.stream().sorted(Comparator.comparing(OrderItem::getGoodsId)).toList()
                .equals(other.items.stream().sorted(Comparator.comparing(OrderItem::getGoodsId)).toList());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Order other)) {
            return false;
        }
        return this == other
                || this.id.equals(other.id)
                && this.status == other.status
                && this.purchaser.equals(other.purchaser)
                && this.time.equals(other.time)
                && this.itemsEquals(other);
    }
}
