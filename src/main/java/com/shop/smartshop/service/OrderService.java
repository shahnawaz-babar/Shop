package com.shop.smartshop.service;

import com.shop.smartshop.dto.CreateOrderRequest;
import com.shop.smartshop.dto.OrderResponseDTO;
import jakarta.validation.Valid;

public interface OrderService {
    OrderResponseDTO placeOrder(Long currentUserId, @Valid CreateOrderRequest request);
}
