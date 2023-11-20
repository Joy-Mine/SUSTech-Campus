package com.sustech.campus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("bus")
public class Bus {
    @TableId(type = IdType.INPUT)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Bus other)) {
            return false;
        }
        return this == other
                || this.name.equals(other.name);
    }

    @Override
    public String toString() {
        return "Bus{" +
                "name='" + this.name + "'" +
                '}';
    }
}
