package com.example.logistics_demo.courier.service.impl;

import com.example.logistics_demo.courier.dto.CourierRegisterRequest;
import com.example.logistics_demo.courier.dto.CourierLoginRequest;
import com.example.logistics_demo.courier.dto.CourierInfoResponse;
import com.example.logistics_demo.courier.model.Courier;
import com.example.logistics_demo.courier.repository.CourierRepository;
import com.example.logistics_demo.courier.service.CourierService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.logistics_demo.user.repository.OrderRepository;
import com.example.logistics_demo.user.model.Order;
import com.example.logistics_demo.user.model.LogisticsTrack;
import com.example.logistics_demo.common.MailService;
import com.example.logistics_demo.user.repository.UserRepository;
import java.util.List;
import com.example.logistics_demo.common.GeoUtil;

@Service
public class CourierServiceImpl implements CourierService {
    @Autowired
    private CourierRepository courierRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MailService mailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GeoUtil geoUtil;

    @Override
    public String register(CourierRegisterRequest request) {
        if (courierRepository.findByUsername(request.getUsername()) != null) {
            return "用户名已存在";
        }
        Courier courier = new Courier();
        BeanUtils.copyProperties(request, courier);
        courierRepository.save(courier);
        return "注册成功";
    }

    @Override
    public com.example.logistics_demo.courier.model.Courier login(CourierLoginRequest request) {
        com.example.logistics_demo.courier.model.Courier courier = courierRepository.findByUsername(request.getUsername());
        if (courier == null || !courier.getPassword().equals(request.getPassword())) {
            return null;
        }
        return courier;
    }

    @Override
    public CourierInfoResponse getInfo(String username) {
        Courier courier = courierRepository.findByUsername(username);
        if (courier == null) return null;
        CourierInfoResponse resp = new CourierInfoResponse();
        BeanUtils.copyProperties(courier, resp);
        return resp;
    }

    @Override
    public String updatePassword(String username, String oldPassword, String newPassword) {
        Courier courier = courierRepository.findByUsername(username);
        if (courier == null || !courier.getPassword().equals(oldPassword)) {
            return "原密码错误";
        }
        courier.setPassword(newPassword);
        courierRepository.save(courier);
        return "修改成功";
    }

    @Override
    public String uploadStation(com.example.logistics_demo.courier.dto.UploadStationRequest request) {
        com.example.logistics_demo.user.model.Order order = orderRepository.findByOrderNo(request.getOrderNo());
        if (order == null) return "订单不存在";
        Courier courier = courierRepository.findByUsername(request.getCourierUsername());
        if (courier == null) return "快递员不存在";
        if (order.getCourierId() == null || !order.getCourierId().equals(courier.getId())) {
            // 绑定快递员
            order.setCourierId(courier.getId());
        }
        // 只有未上传过"运输中"轨迹时才插入
        java.util.List<com.example.logistics_demo.user.model.LogisticsTrack> trackList = order.getTrackList();
        if (trackList == null) trackList = new java.util.ArrayList<>();
        boolean hasTransit = false;
        for (com.example.logistics_demo.user.model.LogisticsTrack t : trackList) {
            if ("运输中".equals(t.getStatus())) {
                hasTransit = true;
                break;
            }
        }
        if (!hasTransit) {
            com.example.logistics_demo.user.model.LogisticsTrack track = new com.example.logistics_demo.user.model.LogisticsTrack();
            track.setTime(new java.util.Date());
            track.setLocation(request.getStationName());
            track.setOperator(courier.getUsername());
            track.setStatus("运输中");
            // 补全经纬度
            Double[] lnglat = geoUtil.getLngLatByLocation(null, null, null, request.getStationName());
            if (lnglat != null) {
                track.setLng(lnglat[0]);
                track.setLat(lnglat[1]);
            }
            // 插入到倒数第二个位置（寄件和收件之间）
            int insertIdx = Math.max(trackList.size() - 1, 1);
            trackList.add(insertIdx, track);
            order.setTrackList(trackList);
            order.setStatus("运输中");
        }
        orderRepository.save(order);
        // 邮件通知
        com.example.logistics_demo.user.model.User user = userRepository.findById(order.getUserId()).orElse(null);
        if (user != null && user.getEmail() != null) {
            mailService.send(user.getEmail(), "快件运输中", "你的快件运输中，订单号：" + order.getOrderNo());
        }
        return "上传成功";
    }

    @Override
    public String receiveOrder(com.example.logistics_demo.courier.dto.ReceiveOrderRequest request) {
        com.example.logistics_demo.user.model.Order order = orderRepository.findByOrderNo(request.getOrderNo());
        if (order == null) return "订单不存在";
        Courier courier = courierRepository.findByUsername(request.getCourierUsername());
        if (courier == null) return "快递员不存在";
        if (order.getCourierId() != null && !order.getCourierId().equals(courier.getId())) {
            return "订单已被其他快递员接单";
        }
        order.setCourierId(courier.getId());
        // 只有状态为未揽收时才覆盖轨迹
        if ("未揽收".equals(order.getStatus())) {
            order.setStatus("已揽收");
            java.util.List<com.example.logistics_demo.user.model.LogisticsTrack> trackList = order.getTrackList();
            if (trackList != null && !trackList.isEmpty()) {
                for (com.example.logistics_demo.user.model.LogisticsTrack t : trackList) {
                    if (order.getSender().getAddress().equals(t.getLocation()) && "未揽收".equals(t.getStatus())) {
                        t.setStatus("已揽收");
                        t.setOperator(courier.getUsername());
                    }
                }
                order.setTrackList(trackList);
            }
        }
        orderRepository.save(order);
        // 邮件通知
        com.example.logistics_demo.user.model.User user = userRepository.findById(order.getUserId()).orElse(null);
        if (user != null && user.getEmail() != null) {
            mailService.send(user.getEmail(), "快件已揽收", "你的快件已揽收，订单号：" + order.getOrderNo());
        }
        return "接单成功";
    }

    @Override
    public String finishOrder(com.example.logistics_demo.courier.dto.FinishOrderRequest request) {
        com.example.logistics_demo.user.model.Order order = orderRepository.findByOrderNo(request.getOrderNo());
        if (order == null) return "订单不存在";
        Courier courier = courierRepository.findByUsername(request.getCourierUsername());
        if (courier == null) return "快递员不存在";
        if (!courier.getId().equals(order.getCourierId())) return "无权操作该订单";
        // 只有未签收时才追加轨迹
        if (!"已签收".equals(order.getStatus())) {
            order.setStatus("已签收");
            com.example.logistics_demo.user.model.LogisticsTrack track = new com.example.logistics_demo.user.model.LogisticsTrack();
            track.setTime(new java.util.Date());
            track.setLocation(order.getReceiver().getAddress());
            track.setOperator(courier.getUsername());
            track.setStatus("已签收");
            // 补全经纬度
            Double[] lnglat = geoUtil.getLngLatByLocation(
                order.getReceiver().getProvince(),
                order.getReceiver().getCity(),
                order.getReceiver().getDistrict(),
                order.getReceiver().getAddress()
            );
            if (lnglat != null) {
                track.setLng(lnglat[0]);
                track.setLat(lnglat[1]);
            }
            java.util.List<com.example.logistics_demo.user.model.LogisticsTrack> trackList = order.getTrackList();
            if (trackList == null) trackList = new java.util.ArrayList<>();
            trackList.add(track);
            order.setTrackList(trackList);
        }
        orderRepository.save(order);
        // 邮件通知
        com.example.logistics_demo.user.model.User user = userRepository.findById(order.getUserId()).orElse(null);
        if (user != null && user.getEmail() != null) {
            mailService.send(user.getEmail(), "快件已签收", "你的快件已签收，订单号：" + order.getOrderNo());
        }
        return "配送完成";
    }

    @Override
    public java.util.List<com.example.logistics_demo.user.dto.OrderResponse> listMyOrders(String courierUsername) {
        Courier courier = courierRepository.findByUsername(courierUsername);
        if (courier == null) return java.util.Collections.emptyList();
        java.util.List<com.example.logistics_demo.user.model.Order> orderList = orderRepository.findAll();
        java.util.List<com.example.logistics_demo.user.dto.OrderResponse> respList = new java.util.ArrayList<>();
        for (com.example.logistics_demo.user.model.Order order : orderList) {
            if (courier.getId().equals(order.getCourierId())) {
                com.example.logistics_demo.user.dto.OrderResponse resp = new com.example.logistics_demo.user.dto.OrderResponse();
                org.springframework.beans.BeanUtils.copyProperties(order, resp);
                respList.add(resp);
            }
        }
        return respList;
    }

    @Override
    public int getPerformance(String courierUsername) {
        Courier courier = courierRepository.findByUsername(courierUsername);
        if (courier == null) return 0;
        java.util.List<com.example.logistics_demo.user.model.Order> orderList = orderRepository.findAll();
        int count = 0;
        for (com.example.logistics_demo.user.model.Order order : orderList) {
            if (courier.getId().equals(order.getCourierId()) && "已签收".equals(order.getStatus())) {
                count++;
            }
        }
        return count;
    }

    @Override
    public com.example.logistics_demo.user.dto.OrderResponse getOrderDetail(String orderNo) {
        com.example.logistics_demo.user.model.Order order = orderRepository.findByOrderNo(orderNo);
        if (order == null) return null;
        com.example.logistics_demo.user.dto.OrderResponse resp = new com.example.logistics_demo.user.dto.OrderResponse();
        org.springframework.beans.BeanUtils.copyProperties(order, resp);
        return resp;
    }

    @Override
    public List<com.example.logistics_demo.user.model.Order> listUnassignedOrders() {
        List<com.example.logistics_demo.user.model.Order> all = orderRepository.findAll();
        List<com.example.logistics_demo.user.model.Order> result = new java.util.ArrayList<>();
        for (com.example.logistics_demo.user.model.Order order : all) {
            if ((order.getCourierId() == null || order.getCourierId().isEmpty()) && "未揽收".equals(order.getStatus())) {
                result.add(order);
            }
        }
        return result;
    }
} 