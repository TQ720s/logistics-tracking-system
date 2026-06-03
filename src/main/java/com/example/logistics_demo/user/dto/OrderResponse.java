package com.example.logistics_demo.user.dto;

import com.example.logistics_demo.user.model.Address;
import com.example.logistics_demo.user.model.LogisticsTrack;
import java.util.List;

public class OrderResponse {
    private String orderNo;
    private Address sender;
    private Address receiver;
    private double weight;
    private double fee;
    private String status;
    private List<LogisticsTrack> trackList;
    private java.util.Date createTime;
    // getter/setter
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public Address getSender() { return sender; }
    public void setSender(Address sender) { this.sender = sender; }
    public Address getReceiver() { return receiver; }
    public void setReceiver(Address receiver) { this.receiver = receiver; }
    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }
    public double getFee() { return fee; }
    public void setFee(double fee) { this.fee = fee; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public List<LogisticsTrack> getTrackList() { return trackList; }
    public void setTrackList(List<LogisticsTrack> trackList) { this.trackList = trackList; }
    public java.util.Date getCreateTime() { return createTime; }
    public void setCreateTime(java.util.Date createTime) { this.createTime = createTime; }
} 