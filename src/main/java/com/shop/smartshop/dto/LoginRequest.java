package com.shop.smartshop.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "Mobile number is required")
    private String mobileNumber;

}