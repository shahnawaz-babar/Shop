package com.shop.smartshop.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOrderRequest {

    @NotNull(message = "Shipping address is required")
    private Long shippingAddressId;

    // Optional: coupon code, notes, payment method etc.
    private String couponCode;

}
