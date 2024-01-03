package com.sustech.campus.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("route")
public class Route {
    @TableField(value = "busId")
    private Long busId;

    @TableField(value = "stationId")
    private Long stationId;

    @TableField(value = "stopOrder")
    private int stopOrder;

    public Route() {
    }

    public Route(Long busId, Long stationId, int stopOrder) {
        this.busId = busId;
        this.stationId = stationId;
        this.stopOrder = stopOrder;
    }

    public Long getBusId() {
        return busId;
    }

    public void setBusId(Long busId) {
        this.busId = busId;
    }

    public Long getStationId() {
        return stationId;
    }

    public void setStationId(Long stationId) {
        this.stationId = stationId;
    }

    public int getStopOrder() {
        return stopOrder;
    }

    public void setStopOrder(int stopOrder) {
        this.stopOrder = stopOrder;
    }
}
