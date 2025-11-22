package com.shop.smartshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CartResponseDTO {
    Long cartId;
    List<CartItemResponseDTO> items;
    BigDecimal totalAmount;
    String currency;
    String status;
}
