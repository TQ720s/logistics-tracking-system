package com.example.logistics_demo.admin.service;

import com.example.logistics_demo.admin.dto.AdminRegisterRequest;
import com.example.logistics_demo.admin.dto.AdminLoginRequest;
import com.example.logistics_demo.admin.dto.AdminInfoResponse;
import com.example.logistics_demo.admin.model.Admin;

public interface AdminService {
    String register(AdminRegisterRequest request);
    Admin login(AdminLoginRequest request);
    AdminInfoResponse getInfo(String username);
    String updatePassword(String username, String oldPassword, String newPassword);
    // 用户管理
    java.util.List<com.example.logistics_demo.user.model.User> listUsers();
    String addUser(com.example.logistics_demo.user.model.User user);
    String updateUser(com.example.logistics_demo.user.model.User user);
    String deleteUser(String userId);
    // 快递员管理
    java.util.List<com.example.logistics_demo.courier.model.Courier> listCouriers();
    String addCourier(com.example.logistics_demo.courier.model.Courier courier);
    String updateCourier(com.example.logistics_demo.courier.model.Courier courier);
    String deleteCourier(String courierId);
    // 订单管理
    java.util.List<com.example.logistics_demo.user.model.Order> listOrders();
    com.example.logistics_demo.user.model.Order getOrderDetail(String orderNo);
} 