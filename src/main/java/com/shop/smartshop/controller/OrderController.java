package com.shop.smartshop.controller;

import com.shop.smartshop.dto.ApiResponse;
import com.shop.smartshop.dto.CreateOrderRequest;
import com.shop.smartshop.dto.OrderResponseDTO;
import com.shop.smartshop.repository.OrderRepository;
import com.shop.smartshop.security.UserPrincipal;
import com.shop.smartshop.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> placeOrder(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody CreateOrderRequest request
    ) {
        log.info("Placing order for user {}", currentUser.getId());

        OrderResponseDTO order = orderService.placeOrder(currentUser.getId(), request);

        return ResponseEntity.ok(
                ApiResponse.success("Order placed successfully", order)
        );
    }


}
