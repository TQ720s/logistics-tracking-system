package com.example.logistics_demo.courier.repository;

import com.example.logistics_demo.courier.model.Courier;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CourierRepository extends MongoRepository<Courier, String> {
    Courier findByUsername(String username);
} 