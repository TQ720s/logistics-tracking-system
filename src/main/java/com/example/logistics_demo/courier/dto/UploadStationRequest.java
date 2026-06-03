package com.example.logistics_demo.courier.dto;

public class UploadStationRequest {
    private String orderNo;
    private String courierUsername;
    private String stationName;
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public String getCourierUsername() { return courierUsername; }
    public void setCourierUsername(String courierUsername) { this.courierUsername = courierUsername; }
    public String getStationName() { return stationName; }
    public void setStationName(String stationName) { this.stationName = stationName; }
} 