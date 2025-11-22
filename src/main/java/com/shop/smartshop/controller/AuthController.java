package com.shop.smartshop.controller;


import com.shop.smartshop.dto.*;
import com.shop.smartshop.entity.User;
import com.shop.smartshop.exception.ResourceNotFoundException;
import com.shop.smartshop.repository.UserRepository;
import com.shop.smartshop.service.AuthService;
import com.shop.smartshop.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/auth")
@Slf4j
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    // generate otp

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Received registration request for mobile: {}, email: {}", request.getMobileNumber(), request.getEmail());
        RegisterResponse response = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success(response.getMessage(), response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@Valid @RequestBody LoginRequest request) {  //OtpSent method
        log.info("Received login request for mobile: {}", request.getMobileNumber());
        TokenResponse response = authService.login(request);
        ApiResponse<TokenResponse> apiResponse = ApiResponse.success("OTP sent successfully", response);
        return ResponseEntity.ok(apiResponse);
    }


    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<TokenResponse>> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        log.info("Received OTP verification request for: {}, code: {}, type: {}",
                request.getIdentifier(), request.getOtp());
        try {
            TokenResponse response = authService.verifyOtp(request);
            log.info("OTP verification successful for: {}", request.getIdentifier());
            ApiResponse<TokenResponse> apiResponse = ApiResponse.success("OTP verified successfully", response);
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            log.error("Error verifying OTP: {}", e.getMessage(), e);
            throw e;
        }
    }

    @DeleteMapping("/delete-user/{id}")
    public ResponseEntity<ApiResponse<?>> deleteUser(@PathVariable long id)
    {
        User user=userRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("User Not Found"));
        userRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("User Deleted Succesfully"));
    }

    @PostMapping("/login_byPassword")
    public ResponseEntity<ApiResponse<?>> loginByPassword(@RequestBody LoginRequestByPasswordDTO request)
    {
        log.info("Received login request for mobile: {}", request.getName());
        TokenResponse response = authService.loginByPassword(request);
        ApiResponse<TokenResponse> apiResponse = ApiResponse.success("OTP sent successfully", response);
        return ResponseEntity.ok(apiResponse);
    }


}
