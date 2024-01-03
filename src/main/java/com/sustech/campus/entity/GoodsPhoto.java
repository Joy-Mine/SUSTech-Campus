package com.sustech.campus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;

@TableName("goods_photo")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GoodsPhoto {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField(value = "goodsId")
    private Long goodsId;

    private String path;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "GoodsPhoto{" +
                "id=" + id +
                ", goodsId=" + goodsId +
                ", path='" + path + "'" +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof GoodsPhoto other)) {
            return false;
        }
        return this == other
                || this.id.equals(other.id)
                && this.goodsId.equals(other.goodsId)
                && this.path.equals(other.path);
    }
}
