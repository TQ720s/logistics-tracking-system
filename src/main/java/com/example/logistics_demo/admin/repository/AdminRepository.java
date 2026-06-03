package com.example.logistics_demo.admin.repository;

import com.example.logistics_demo.admin.model.Admin;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AdminRepository extends MongoRepository<Admin, String> {
    Admin findByUsername(String username);
} 