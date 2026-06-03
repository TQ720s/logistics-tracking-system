package com.example.logistics_demo.user.dto;

import com.example.logistics_demo.user.model.Address;
import com.example.logistics_demo.user.dto.AddressRequest;

public class OrderRequest {
    private String username;
    private AddressRequest sender;
    private AddressRequest receiver;
    private double weight;
    // getter/setter
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public AddressRequest getSender() { return sender; }
    public void setSender(AddressRequest sender) { this.sender = sender; }
    public AddressRequest getReceiver() { return receiver; }
    public void setReceiver(AddressRequest receiver) { this.receiver = receiver; }
    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }
} 