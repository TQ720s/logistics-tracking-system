package com.example.logistics_demo.courier.dto;

public class ReceiveOrderRequest {
    private String orderNo;
    private String courierUsername;
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public String getCourierUsername() { return courierUsername; }
    public void setCourierUsername(String courierUsername) { this.courierUsername = courierUsername; }
} 