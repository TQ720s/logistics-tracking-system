package com.example.logistics_demo.courier.controller;

import com.example.logistics_demo.courier.dto.CourierRegisterRequest;
import com.example.logistics_demo.courier.dto.CourierLoginRequest;
import com.example.logistics_demo.courier.dto.CourierInfoResponse;
import com.example.logistics_demo.courier.service.CourierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/courier")
public class CourierController {
    @Autowired
    private CourierService courierService;

    @PostMapping("/register")
    public String register(@RequestBody CourierRegisterRequest request) {
        return courierService.register(request);
    }

    @PostMapping("/login")
    public Object login(@RequestBody CourierLoginRequest request) {
        com.example.logistics_demo.courier.model.Courier courier = courierService.login(request);
        if (courier == null) {
            return Map.of("error", "用户名或密码错误");
        }
        return Map.of("username", courier.getUsername(), "role", "courier");
    }

    @GetMapping("/info")
    public CourierInfoResponse info(@RequestParam String username) {
        return courierService.getInfo(username);
    }

    @PostMapping("/updatePassword")
    public String updatePassword(@RequestParam String username, @RequestParam String oldPassword, @RequestParam String newPassword) {
        return courierService.updatePassword(username, oldPassword, newPassword);
    }

    @PostMapping("/order/uploadStation")
    public String uploadStation(@RequestBody com.example.logistics_demo.courier.dto.UploadStationRequest request) {
        return courierService.uploadStation(request);
    }

    @PostMapping("/order/receive")
    public String receiveOrder(@RequestBody com.example.logistics_demo.courier.dto.ReceiveOrderRequest request) {
        return courierService.receiveOrder(request);
    }

    @PostMapping("/order/finish")
    public String finishOrder(@RequestBody com.example.logistics_demo.courier.dto.FinishOrderRequest request) {
        return courierService.finishOrder(request);
    }

    @GetMapping("/order/list")
    public java.util.List<com.example.logistics_demo.user.dto.OrderResponse> listMyOrders(@RequestParam String courierUsername) {
        return courierService.listMyOrders(courierUsername);
    }

    @GetMapping("/order/detail")
    public com.example.logistics_demo.user.dto.OrderResponse getOrderDetail(@RequestParam String orderNo) {
        return courierService.getOrderDetail(orderNo);
    }

    @GetMapping("/performance")
    public int getPerformance(@RequestParam String courierUsername) {
        return courierService.getPerformance(courierUsername);
    }

    @GetMapping("/order/unassigned")
    public List<com.example.logistics_demo.user.model.Order> listUnassignedOrders() {
        return courierService.listUnassignedOrders();
    }
} 