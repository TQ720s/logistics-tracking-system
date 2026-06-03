package com.example.logistics_demo.user.model;

import java.util.Date;

public class LogisticsTrack {
    private Date time;
    private String location;
    private String operator;
    private String status; // 状态描述
    private Double lng; // 经度
    private Double lat; // 纬度

    public Date getTime() { return time; }
    public void setTime(Date time) { this.time = time; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getOperator() { return operator; }
    public void setOperator(String operator) { this.operator = operator; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Double getLng() { return lng; }
    public void setLng(Double lng) { this.lng = lng; }
    public Double getLat() { return lat; }
    public void setLat(Double lat) { this.lat = lat; }
} 