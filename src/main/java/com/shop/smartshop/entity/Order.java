package com.shop.smartshop.entity;

import com.shop.smartshop.enums.OrderStatus;
import com.shop.smartshop.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Data
@Table(name = "orders")
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private  String paymentMethod;
    private PaymentStatus paymentStatus;
    private OrderStatus orderStatus;
    private String trackingLatitude;
    private String trackingLongitude;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String paymentReference; // Razorpay/Stripe ID etc.
    @OneToOne
    @JoinColumn(name="user_id")
    private User user;
    @OneToOne
    @JoinColumn(name="address_id")
    private Address address;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();
}
