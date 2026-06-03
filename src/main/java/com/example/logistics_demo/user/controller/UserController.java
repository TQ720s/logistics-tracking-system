package com.example.logistics_demo.user.controller;

import com.example.logistics_demo.user.dto.UserRegisterRequest;
import com.example.logistics_demo.user.dto.UserLoginRequest;
import com.example.logistics_demo.user.dto.UserInfoResponse;
import com.example.logistics_demo.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public String register(@RequestBody UserRegisterRequest request) {
        return userService.register(request);
    }

    @PostMapping("/login")
    public Object login(@RequestBody UserLoginRequest request) {
        com.example.logistics_demo.user.model.User user = userService.login(request);
        if (user == null) {
            return Map.of("error", "用户名或密码错误");
        }
        return Map.of("username", user.getUsername(), "role", "user");
    }

    @GetMapping("/info")
    public UserInfoResponse info(@RequestParam String username) {
        return userService.getUserInfo(username);
    }

    @PostMapping("/updatePassword")
    public String updatePassword(@RequestParam String username, @RequestParam String oldPassword, @RequestParam String newPassword) {
        return userService.updatePassword(username, oldPassword, newPassword);
    }

    @PostMapping("/address/add")
    public String addAddress(@RequestBody com.example.logistics_demo.user.dto.AddressRequest request) {
        com.example.logistics_demo.user.model.Address address = new com.example.logistics_demo.user.model.Address();
        address.setName(request.getName());
        address.setPhone(request.getPhone());
        address.setProvince(request.getProvince());
        address.setCity(request.getCity());
        address.setDistrict(request.getDistrict());
        address.setAddress(request.getAddress());
        return userService.addAddress(request.getUsername(), address);
    }

    @PostMapping("/address/delete")
    public String deleteAddress(@RequestBody com.example.logistics_demo.user.dto.AddressRequest request) {
        com.example.logistics_demo.user.model.Address address = new com.example.logistics_demo.user.model.Address();
        address.setName(request.getName());
        address.setPhone(request.getPhone());
        address.setProvince(request.getProvince());
        address.setCity(request.getCity());
        address.setDistrict(request.getDistrict());
        address.setAddress(request.getAddress());
        return userService.deleteAddress(request.getUsername(), address);
    }

    @GetMapping("/address/list")
    public java.util.List<com.example.logistics_demo.user.model.Address> listAddress(@RequestParam String username) {
        return userService.listAddress(username);
    }

    @PostMapping("/order/create")
    public String createOrder(@RequestBody com.example.logistics_demo.user.dto.OrderRequest request) {
        return userService.createOrder(request);
    }

    @GetMapping("/order/list")
    public java.util.List<com.example.logistics_demo.user.dto.OrderResponse> listOrders(@RequestParam String username) {
        return userService.listOrders(username);
    }

    @GetMapping("/order/detail")
    public com.example.logistics_demo.user.dto.OrderResponse getOrderDetail(@RequestParam String orderNo) {
        return userService.getOrderDetail(orderNo);
    }
} 