package com.example.logistics_demo.user.repository;

import com.example.logistics_demo.user.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface OrderRepository extends MongoRepository<Order, String> {
    List<Order> findByUserId(String userId);
    Order findByOrderNo(String orderNo);
} 