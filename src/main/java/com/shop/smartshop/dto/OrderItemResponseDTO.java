package com.shop.smartshop.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderItemResponseDTO {
    private Long productId;
    private String productName;
    private double priceAtTime;
    private int quantity;
    private BigDecimal subTotal;
}