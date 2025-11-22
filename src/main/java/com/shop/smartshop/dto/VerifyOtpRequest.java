package com.shop.smartshop.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyOtpRequest {
    @NotBlank(message = "Identifier (mobile/email) is required")
    private String identifier;

    @NotBlank(message = "OTP code is required")
    private String otp;

}