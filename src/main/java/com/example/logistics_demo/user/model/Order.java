package com.example.logistics_demo.user.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Document(collection = "orders")
public class Order {
    @Id
    private String id;
    private String orderNo;
    private String userId;
    private Address sender;
    private Address receiver;
    private double weight;
    private double fee;
    private List<LogisticsTrack> trackList;
    private String status; // 已揽收、运输中、已签收
    private String courierId; // 快递员id
    private java.util.Date createTime;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public Address getSender() { return sender; }
    public void setSender(Address sender) { this.sender = sender; }
    public Address getReceiver() { return receiver; }
    public void setReceiver(Address receiver) { this.receiver = receiver; }
    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }
    public double getFee() { return fee; }
    public void setFee(double fee) { this.fee = fee; }
    public List<LogisticsTrack> getTrackList() { return trackList; }
    public void setTrackList(List<LogisticsTrack> trackList) { this.trackList = trackList; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCourierId() { return courierId; }
    public void setCourierId(String courierId) { this.courierId = courierId; }
    public java.util.Date getCreateTime() { return createTime; }
    public void setCreateTime(java.util.Date createTime) { this.createTime = createTime; }
} 