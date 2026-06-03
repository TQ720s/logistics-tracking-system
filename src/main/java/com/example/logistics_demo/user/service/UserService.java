package com.example.logistics_demo.user.service;

import com.example.logistics_demo.user.dto.UserRegisterRequest;
import com.example.logistics_demo.user.dto.UserLoginRequest;
import com.example.logistics_demo.user.dto.UserInfoResponse;
import com.example.logistics_demo.user.model.User;

public interface UserService {
    String register(UserRegisterRequest request);
    User login(UserLoginRequest request);
    UserInfoResponse getUserInfo(String username);
    String updatePassword(String username, String oldPassword, String newPassword);
    String addAddress(String username, com.example.logistics_demo.user.model.Address address);
    String deleteAddress(String username, com.example.logistics_demo.user.model.Address address);
    java.util.List<com.example.logistics_demo.user.model.Address> listAddress(String username);
    String createOrder(com.example.logistics_demo.user.dto.OrderRequest request);
    java.util.List<com.example.logistics_demo.user.dto.OrderResponse> listOrders(String username);
    com.example.logistics_demo.user.dto.OrderResponse getOrderDetail(String orderNo);
} 