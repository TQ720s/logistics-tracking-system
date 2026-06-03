package com.example.logistics_demo.user.dto;

import com.example.logistics_demo.user.model.Address;
import java.util.List;

public class UserInfoResponse {
    private String id;
    private String username;
    private String name;
    private String phone;
    private String email;
    private List<Address> addressList;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public List<Address> getAddressList() { return addressList; }
    public void setAddressList(List<Address> addressList) { this.addressList = addressList; }
} 