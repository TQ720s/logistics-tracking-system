package com.example.logistics_demo.admin.service.impl;

import com.example.logistics_demo.admin.dto.AdminRegisterRequest;
import com.example.logistics_demo.admin.dto.AdminLoginRequest;
import com.example.logistics_demo.admin.dto.AdminInfoResponse;
import com.example.logistics_demo.admin.model.Admin;
import com.example.logistics_demo.admin.repository.AdminRepository;
import com.example.logistics_demo.admin.service.AdminService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.logistics_demo.user.repository.UserRepository;
import com.example.logistics_demo.courier.repository.CourierRepository;
import com.example.logistics_demo.user.repository.OrderRepository;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CourierRepository courierRepository;
    @Autowired
    private OrderRepository orderRepository;

    @Override
    public String register(AdminRegisterRequest request) {
        if (adminRepository.findByUsername(request.getUsername()) != null) {
            return "用户名已存在";
        }
        Admin admin = new Admin();
        BeanUtils.copyProperties(request, admin);
        adminRepository.save(admin);
        return "注册成功";
    }

    @Override
    public com.example.logistics_demo.admin.model.Admin login(AdminLoginRequest request) {
        com.example.logistics_demo.admin.model.Admin admin = adminRepository.findByUsername(request.getUsername());
        if (admin == null || !admin.getPassword().equals(request.getPassword())) {
            return null;
        }
        return admin;
    }

    @Override
    public AdminInfoResponse getInfo(String username) {
        Admin admin = adminRepository.findByUsername(username);
        if (admin == null) return null;
        AdminInfoResponse resp = new AdminInfoResponse();
        BeanUtils.copyProperties(admin, resp);
        return resp;
    }

    @Override
    public String updatePassword(String username, String oldPassword, String newPassword) {
        Admin admin = adminRepository.findByUsername(username);
        if (admin == null || !admin.getPassword().equals(oldPassword)) {
            return "原密码错误";
        }
        admin.setPassword(newPassword);
        adminRepository.save(admin);
        return "修改成功";
    }

    // 用户管理
    @Override
    public java.util.List<com.example.logistics_demo.user.model.User> listUsers() {
        return userRepository.findAll();
    }
    @Override
    public String addUser(com.example.logistics_demo.user.model.User user) {
        if (userRepository.findByUsername(user.getUsername()) != null) return "用户名已存在";
        userRepository.save(user);
        return "添加成功";
    }
    @Override
    public String updateUser(com.example.logistics_demo.user.model.User user) {
        if (user.getId() == null) return "用户ID不能为空";
        userRepository.save(user);
        return "修改成功";
    }
    @Override
    public String deleteUser(String userId) {
        userRepository.deleteById(userId);
        return "删除成功";
    }
    // 快递员管理
    @Override
    public java.util.List<com.example.logistics_demo.courier.model.Courier> listCouriers() {
        return courierRepository.findAll();
    }
    @Override
    public String addCourier(com.example.logistics_demo.courier.model.Courier courier) {
        if (courierRepository.findByUsername(courier.getUsername()) != null) return "用户名已存在";
        courierRepository.save(courier);
        return "添加成功";
    }
    @Override
    public String updateCourier(com.example.logistics_demo.courier.model.Courier courier) {
        if (courier.getId() == null) return "快递员ID不能为空";
        courierRepository.save(courier);
        return "修改成功";
    }
    @Override
    public String deleteCourier(String courierId) {
        courierRepository.deleteById(courierId);
        return "删除成功";
    }
    // 订单管理
    @Override
    public java.util.List<com.example.logistics_demo.user.model.Order> listOrders() {
        return orderRepository.findAll();
    }
    @Override
    public com.example.logistics_demo.user.model.Order getOrderDetail(String orderNo) {
        return orderRepository.findByOrderNo(orderNo);
    }
} 