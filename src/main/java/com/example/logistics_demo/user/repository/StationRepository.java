package com.example.logistics_demo.user.repository;

import com.example.logistics_demo.user.model.Station;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StationRepository extends MongoRepository<Station, String> {
    Station findByName(String name);
} 