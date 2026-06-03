package com.example.logistics_demo.courier.service;

import com.example.logistics_demo.courier.dto.CourierRegisterRequest;
import com.example.logistics_demo.courier.dto.CourierLoginRequest;
import com.example.logistics_demo.courier.dto.CourierInfoResponse;
import com.example.logistics_demo.courier.dto.UploadStationRequest;
import com.example.logistics_demo.courier.dto.ReceiveOrderRequest;
import com.example.logistics_demo.courier.dto.FinishOrderRequest;
import com.example.logistics_demo.user.dto.OrderResponse;
import com.example.logistics_demo.courier.model.Courier;
import com.example.logistics_demo.user.model.Order;
import java.util.List;

public interface CourierService {
    String register(CourierRegisterRequest request);
    Courier login(CourierLoginRequest request);
    CourierInfoResponse getInfo(String username);
    String updatePassword(String username, String oldPassword, String newPassword);
    String uploadStation(UploadStationRequest request);
    String receiveOrder(ReceiveOrderRequest request);
    String finishOrder(FinishOrderRequest request);
    java.util.List<OrderResponse> listMyOrders(String courierUsername);
    int getPerformance(String courierUsername);
    com.example.logistics_demo.user.dto.OrderResponse getOrderDetail(String orderNo);
    List<Order> listUnassignedOrders();
} 