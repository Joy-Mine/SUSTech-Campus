package com.sustech.campus.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

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

    @TableField(exist = false)
    private List<GoodsPhoto> photos;

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

    public List<GoodsPhoto> getPhotos() {
        return photos;
    }

    public void setPhotos(List<GoodsPhoto> photos) {
        this.photos = photos;
    }

    private boolean photosEquals(Goods other) {
        if (this.photos == null || other.photos == null) {
            return this.photos == other.photos;
        }
        return this.photos.stream().sorted(Comparator.comparing(GoodsPhoto::getId)).toList()
                .equals(other.photos.stream().sorted(Comparator.comparing(GoodsPhoto::getId)).toList());
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
                ", photos=" + photos +
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
                && this.hidden.equals(other.hidden)
                && this.photosEquals(other);
    }
}
