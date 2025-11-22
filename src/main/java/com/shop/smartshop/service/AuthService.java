package com.shop.smartshop.service;

import com.shop.smartshop.dto.*;

public interface AuthService {

    RegisterResponse register(RegisterRequest request);

    TokenResponse login(LoginRequest request);

    TokenResponse verifyOtp(VerifyOtpRequest request);

    TokenResponse loginByPassword(LoginRequestByPasswordDTO request);
}
