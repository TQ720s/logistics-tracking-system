package com.example.logistics_demo.user.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "stations")
public class Station {
    @Id
    private String id;
    private String name;
    private Double lng;
    private Double lat;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Double getLng() { return lng; }
    public void setLng(Double lng) { this.lng = lng; }
    public Double getLat() { return lat; }
    public void setLat(Double lat) { this.lat = lat; }
} 