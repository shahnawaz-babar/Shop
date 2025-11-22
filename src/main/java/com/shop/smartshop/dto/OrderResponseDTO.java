package com.shop.smartshop.dto;

import com.shop.smartshop.enums.OrderStatus;
import com.shop.smartshop.enums.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OrderResponseDTO {
    private Long orderId;
    private OrderStatus status;
    private PaymentStatus paymentStatus;
    private BigDecimal totalAmount;
    private List<OrderItemResponseDTO> items;
    private LocalDateTime createdAt;
}