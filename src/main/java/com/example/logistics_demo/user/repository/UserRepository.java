package com.example.logistics_demo.user.repository;

import com.example.logistics_demo.user.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    User findByUsername(String username);
} 