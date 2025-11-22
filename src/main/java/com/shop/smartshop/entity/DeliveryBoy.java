package com.shop.smartshop.entity;

import jakarta.persistence.*;

public class DeliveryBoy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private  String name;
    private String phone;
    private double currentLatitude;
    private double currentLongitude;
    @OneToMany
    @JoinColumn(name="order_id")
    private Order order;
}
