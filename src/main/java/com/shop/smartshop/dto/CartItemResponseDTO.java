package com.shop.smartshop.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemResponseDTO {
    private Long itemId;
    private Long productId;
    private String productName;
    private double priceAtTime;
    private int quantity;
    private BigDecimal subTotal;
}