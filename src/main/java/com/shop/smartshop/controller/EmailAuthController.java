package com.shop.smartshop.controller;


import com.shop.smartshop.dto.ApiResponse;
import com.shop.smartshop.dto.LoginRequest;
import com.shop.smartshop.dto.TokenResponse;
import com.shop.smartshop.repository.UserRepository;
import com.shop.smartshop.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/auth2")
@RestController
@RequiredArgsConstructor
@Slf4j
public class EmailAuthController {

    private final UserRepository userRepository;
    private final AuthService authService;


    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@Valid @RequestBody LoginRequest request) {  //OtpSent method
        log.info("Received login request for mobile: {}", request.getMobileNumber());
        TokenResponse response = authService.loginWithEmail(request);
        ApiResponse<TokenResponse> apiResponse = ApiResponse.success("OTP sent successfully", response);
        return ResponseEntity.ok(apiResponse);
    }




}
