package com.shop.smartshop.service;

import com.shop.smartshop.dto.*;
import jakarta.validation.Valid;

public interface AuthService {

    RegisterResponse register(RegisterRequest request);

    TokenResponse login(LoginRequest request);

    TokenResponse verifyOtp(VerifyOtpRequest request);

    TokenResponse loginByPassword(LoginRequestByPasswordDTO request);

    TokenResponse loginWithEmail(@Valid LoginRequest request);
}
