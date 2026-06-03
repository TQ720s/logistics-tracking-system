package com.example.logistics_demo.admin.controller;

import com.example.logistics_demo.admin.dto.AdminRegisterRequest;
import com.example.logistics_demo.admin.dto.AdminLoginRequest;
import com.example.logistics_demo.admin.dto.AdminInfoResponse;
import com.example.logistics_demo.admin.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;

    @PostMapping("/register")
    public String register(@RequestBody AdminRegisterRequest request) {
        return adminService.register(request);
    }

    @PostMapping("/login")
    public Object login(@RequestBody AdminLoginRequest request) {
        com.example.logistics_demo.admin.model.Admin admin = adminService.login(request);
        if (admin == null) {
            return Map.of("error", "用户名或密码错误");
        }
        return Map.of("username", admin.getUsername(), "role", "admin");
    }

    @GetMapping("/info")
    public AdminInfoResponse info(@RequestParam String username) {
        return adminService.getInfo(username);
    }

    @PostMapping("/updatePassword")
    public String updatePassword(@RequestParam String username, @RequestParam String oldPassword, @RequestParam String newPassword) {
        return adminService.updatePassword(username, oldPassword, newPassword);
    }

    // 用户管理
    @GetMapping("/user/list")
    public java.util.List<com.example.logistics_demo.user.model.User> listUsers() {
        return adminService.listUsers();
    }
    @PostMapping("/user/add")
    public String addUser(@RequestBody com.example.logistics_demo.user.model.User user) {
        return adminService.addUser(user);
    }
    @PostMapping("/user/update")
    public String updateUser(@RequestBody com.example.logistics_demo.user.model.User user) {
        return adminService.updateUser(user);
    }
    @PostMapping("/user/delete")
    public String deleteUser(@RequestParam String userId) {
        return adminService.deleteUser(userId);
    }
    // 快递员管理
    @GetMapping("/courier/list")
    public java.util.List<com.example.logistics_demo.courier.model.Courier> listCouriers() {
        return adminService.listCouriers();
    }
    @PostMapping("/courier/add")
    public String addCourier(@RequestBody com.example.logistics_demo.courier.model.Courier courier) {
        return adminService.addCourier(courier);
    }
    @PostMapping("/courier/update")
    public String updateCourier(@RequestBody com.example.logistics_demo.courier.model.Courier courier) {
        return adminService.updateCourier(courier);
    }
    @PostMapping("/courier/delete")
    public String deleteCourier(@RequestParam String courierId) {
        return adminService.deleteCourier(courierId);
    }
    // 订单管理
    @GetMapping("/order/list")
    public java.util.List<com.example.logistics_demo.user.model.Order> listOrders() {
        return adminService.listOrders();
    }
    @GetMapping("/order/detail")
    public com.example.logistics_demo.user.model.Order getOrderDetail(@RequestParam String orderNo) {
        return adminService.getOrderDetail(orderNo);
    }
} 