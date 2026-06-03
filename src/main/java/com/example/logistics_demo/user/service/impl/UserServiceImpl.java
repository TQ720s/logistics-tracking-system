package com.example.logistics_demo.user.service.impl;

import com.example.logistics_demo.user.dto.UserRegisterRequest;
import com.example.logistics_demo.user.dto.UserLoginRequest;
import com.example.logistics_demo.user.dto.UserInfoResponse;
import com.example.logistics_demo.user.model.User;
import com.example.logistics_demo.user.repository.UserRepository;
import com.example.logistics_demo.user.repository.OrderRepository;
import com.example.logistics_demo.user.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.logistics_demo.common.GeoUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private GeoUtil geoUtil;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public String register(UserRegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()) != null) {
            return "用户名已存在";
        }
        User user = new User();
        BeanUtils.copyProperties(request, user);
        userRepository.save(user);
        return "注册成功";
    }

    @Override
    public com.example.logistics_demo.user.model.User login(UserLoginRequest request) {
        com.example.logistics_demo.user.model.User user = userRepository.findByUsername(request.getUsername());
        if (user == null || !user.getPassword().equals(request.getPassword())) {
            return null;
        }
        return user;
    }

    @Override
    public UserInfoResponse getUserInfo(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) return null;
        UserInfoResponse resp = new UserInfoResponse();
        BeanUtils.copyProperties(user, resp);
        return resp;
    }

    @Override
    public String updatePassword(String username, String oldPassword, String newPassword) {
        User user = userRepository.findByUsername(username);
        if (user == null || !user.getPassword().equals(oldPassword)) {
            return "原密码错误";
        }
        user.setPassword(newPassword);
        userRepository.save(user);
        return "修改成功";
    }

    @Override
    public String addAddress(String username, com.example.logistics_demo.user.model.Address address) {
        User user = userRepository.findByUsername(username);
        if (user == null) return "用户不存在";
        java.util.List<com.example.logistics_demo.user.model.Address> list = user.getAddressList();
        if (list == null) list = new java.util.ArrayList<>();
        list.add(address);
        user.setAddressList(list);
        userRepository.save(user);
        // 同步写入redis
        String redisKey = "user:address:" + username;
        redisTemplate.opsForValue().set(redisKey, list, 3, TimeUnit.HOURS);
        return "添加成功";
    }

    @Override
    public String deleteAddress(String username, com.example.logistics_demo.user.model.Address address) {
        User user = userRepository.findByUsername(username);
        if (user == null) return "用户不存在";
        java.util.List<com.example.logistics_demo.user.model.Address> list = user.getAddressList();
        if (list == null) return "无可删除地址";
        boolean removed = list.removeIf(a -> a.getName().equals(address.getName()) && a.getPhone().equals(address.getPhone()) && a.getAddress().equals(address.getAddress()));
        if (!removed) return "未找到该地址";
        user.setAddressList(list);
        userRepository.save(user);
        // 同步写入redis
        String redisKey = "user:address:" + username;
        redisTemplate.opsForValue().set(redisKey, list, 3, TimeUnit.HOURS);
        return "删除成功";
    }

    @Override
    public java.util.List<com.example.logistics_demo.user.model.Address> listAddress(String username) {
        String redisKey = "user:address:" + username;
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        Object cached = ops.get(redisKey);
        if (cached != null && cached instanceof java.util.List) {
            return (java.util.List<com.example.logistics_demo.user.model.Address>) cached;
        }
        // redis没有则查mongodb
        User user = userRepository.findByUsername(username);
        if (user == null) return java.util.Collections.emptyList();
        java.util.List<com.example.logistics_demo.user.model.Address> list = user.getAddressList();
        if (list == null) list = java.util.Collections.emptyList();
        // 写入redis
        ops.set(redisKey, list, 3, TimeUnit.HOURS);
        return list;
    }

    private double calcFee(double weight) {
        if (weight <= 1) return 12.0;
        return 12.0 + (weight - 1) * 3.0;
    }

    private String generateOrderNo() {
        return "OD" + System.currentTimeMillis();
    }

    @Override
    public String createOrder(com.example.logistics_demo.user.dto.OrderRequest request) {
        User user = userRepository.findByUsername(request.getUsername());
        if (user == null) return "用户不存在";
        com.example.logistics_demo.user.model.Order order = new com.example.logistics_demo.user.model.Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(user.getId());
        // AddressRequest -> Address
        com.example.logistics_demo.user.model.Address senderAddr = new com.example.logistics_demo.user.model.Address();
        senderAddr.setName(request.getSender().getName());
        senderAddr.setPhone(request.getSender().getPhone());
        senderAddr.setProvince(request.getSender().getProvince());
        senderAddr.setCity(request.getSender().getCity());
        senderAddr.setDistrict(request.getSender().getDistrict());
        senderAddr.setAddress(request.getSender().getAddress());
        com.example.logistics_demo.user.model.Address receiverAddr = new com.example.logistics_demo.user.model.Address();
        receiverAddr.setName(request.getReceiver().getName());
        receiverAddr.setPhone(request.getReceiver().getPhone());
        receiverAddr.setProvince(request.getReceiver().getProvince());
        receiverAddr.setCity(request.getReceiver().getCity());
        receiverAddr.setDistrict(request.getReceiver().getDistrict());
        receiverAddr.setAddress(request.getReceiver().getAddress());
        order.setSender(senderAddr);
        order.setReceiver(receiverAddr);
        order.setWeight(request.getWeight());
        order.setFee(calcFee(request.getWeight()));
        order.setStatus("未揽收");
        order.setCreateTime(new java.util.Date());
        java.util.List<com.example.logistics_demo.user.model.LogisticsTrack> trackList = new java.util.ArrayList<>();
        // 提取城市信息
        String senderCity = extractCity(senderAddr.getAddress());
        String receiverCity = extractCity(receiverAddr.getAddress());
        // 用完整地址查经纬度
        Double[] senderLngLat = geoUtil.getLngLatByLocation(
            senderAddr.getProvince(),
            senderAddr.getCity(),
            senderAddr.getDistrict(),
            senderAddr.getAddress()
        );
        Double[] receiverLngLat = geoUtil.getLngLatByLocation(
            receiverAddr.getProvince(),
            receiverAddr.getCity(),
            receiverAddr.getDistrict(),
            receiverAddr.getAddress()
        );
        // 首个轨迹点（寄件地址）
        com.example.logistics_demo.user.model.LogisticsTrack track = new com.example.logistics_demo.user.model.LogisticsTrack();
        track.setTime(new java.util.Date());
        track.setLocation(senderAddr.getAddress());
        track.setOperator(user.getUsername());
        track.setStatus("未揽收");
        if (senderLngLat != null) {
            track.setLng(senderLngLat[0]);
            track.setLat(senderLngLat[1]);
        }
        trackList.add(track);
        // 末尾轨迹点（收件地址，用于绘制虚线轨迹）
        com.example.logistics_demo.user.model.LogisticsTrack endTrack = new com.example.logistics_demo.user.model.LogisticsTrack();
        endTrack.setTime(new java.util.Date());
        endTrack.setLocation(receiverAddr.getAddress());
        endTrack.setOperator(null);
        endTrack.setStatus("待配送");
        if (receiverLngLat != null) {
            endTrack.setLng(receiverLngLat[0]);
            endTrack.setLat(receiverLngLat[1]);
        }
        trackList.add(endTrack);
        order.setTrackList(trackList);
        order.setCourierId(null);
        orderRepository.save(order);
        return order.getOrderNo();
    }

    // 提取市级名称（如"北京市朝阳区三里屯"->"北京市"）
    private String extractCity(String address) {
        if (address == null) return null;
        
        // 处理直辖市
        if (address.contains("北京市")) return "北京市";
        if (address.contains("天津市")) return "天津市";
        if (address.contains("上海市")) return "上海市";
        if (address.contains("重庆市")) return "重庆市";
        
        // 匹配其他城市（避免重复的"市"字）
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("([\u4e00-\u9fa5]+市)").matcher(address);
        if (m.find()) {
            String city = m.group(1);
            // 避免重复的"市"字
            if (city.endsWith("市市")) {
                return city.substring(0, city.length() - 1);
            }
            return city;
        }
        
        // 如果没有找到"市"，尝试匹配省份+城市
        m = java.util.regex.Pattern.compile("([\u4e00-\u9fa5]+省[\u4e00-\u9fa5]+市)").matcher(address);
        if (m.find()) {
            return m.group(1);
        }
        
        // 最后的后备方案
        if (address.length() >= 3) return address.substring(0, 3);
        if (address.length() >= 2) return address.substring(0, 2);
        return address;
    }

    @Override
    public java.util.List<com.example.logistics_demo.user.dto.OrderResponse> listOrders(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) return java.util.Collections.emptyList();
        java.util.List<com.example.logistics_demo.user.model.Order> orderList = orderRepository.findByUserId(user.getId());
        // 按创建时间倒序排列
        orderList.sort((a, b) -> b.getCreateTime().compareTo(a.getCreateTime()));
        java.util.List<com.example.logistics_demo.user.dto.OrderResponse> respList = new java.util.ArrayList<>();
        for (com.example.logistics_demo.user.model.Order order : orderList) {
            com.example.logistics_demo.user.dto.OrderResponse resp = new com.example.logistics_demo.user.dto.OrderResponse();
            org.springframework.beans.BeanUtils.copyProperties(order, resp);
            respList.add(resp);
        }
        return respList;
    }

    @Override
    public com.example.logistics_demo.user.dto.OrderResponse getOrderDetail(String orderNo) {
        com.example.logistics_demo.user.model.Order order = orderRepository.findByOrderNo(orderNo);
        if (order == null) return null;
        com.example.logistics_demo.user.dto.OrderResponse resp = new com.example.logistics_demo.user.dto.OrderResponse();
        org.springframework.beans.BeanUtils.copyProperties(order, resp);
        return resp;
    }
} 