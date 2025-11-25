package com.shop.smartshop.serviceimpl;

import com.shop.smartshop.dto.*;
import com.shop.smartshop.entity.User;
import com.shop.smartshop.entity.UserSession;
import com.shop.smartshop.enums.Role;
import com.shop.smartshop.exception.AuthException;
import com.shop.smartshop.exception.BadRequestException;
import com.shop.smartshop.repository.UserRepository;
import com.shop.smartshop.repository.UserSessionRepository;
import com.shop.smartshop.service.AuthService;
import com.shop.smartshop.service.OtpService;
import com.shop.smartshop.util.JwtUtil;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Builder
public class AuthServiceImpl implements AuthService {

    private final OtpService otpService;
    private final UserRepository userRepository;
    private final UserSessionRepository userSessionRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder=new BCryptPasswordEncoder();

    // ====================== REGISTER ======================
    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        log.debug("Registering user with mobile: {}", request.getMobileNumber());

        if (userRepository.findByMobileNumber(request.getMobileNumber()).isPresent()) {
            log.warn("Registration failed - mobile already registered: {}", request.getMobileNumber());
            return RegisterResponse.builder()
                    .success(false)
                    .message("Mobile number already registered")
                    .build();
        }

        if (request.getEmail() != null && userRepository.findByEmail(request.getEmail()).isPresent()) {
            log.warn("Registration failed - email already registered: {}", request.getEmail());
            return RegisterResponse.builder()
                    .success(false)
                    .message("Email already registered")
                    .build();
        }

        String encodedPassword=passwordEncoder.encode(request.getPassword());
        log.info("Encoded password :"+encodedPassword);
        System.out.println("Encoded Password: "+encodedPassword);

        User user = User.builder()
                .name(request.getName())
                .mobileNumber(request.getMobileNumber())
                .email(request.getEmail())
                .address(request.getAddress())
                .roles(Optional.ofNullable(request.getRoles()).orElse(new HashSet<>(Set.of(Role.USER))))
                .mobileVerified(false)
                .password(encodedPassword)
                .build();

        userRepository.save(user);

        if (user.getId() == 1) {
            log.info("First registered user detected (ID=1). Assigning ADMIN role.");

            Set<Role> mutableRoles = new HashSet<>();
            mutableRoles.add(Role.ADMIN);
            mutableRoles.add(Role.USER);

            user.setRoles(mutableRoles);
            userRepository.save(user);
        }


        log.info("User registered successfully: {}", user.getId());

        return RegisterResponse.builder()
                .success(true)
                .message("Registration successful. Please verify your mobile number.")
                .build();
    }


    // ====================== LOGIN (SEND OTP) ======================
    @Override
    public TokenResponse login(LoginRequest request) {
        log.info("Login attempt for mobile: {}", request.getMobileNumber());

        User user = userRepository.findByMobileNumber(request.getMobileNumber())
                .orElseThrow(() -> new AuthException("User not found with this mobile number"));

        String otp = otpService.generateOtp(request.getMobileNumber());
        log.info("OTP sent to {}: {}", request.getMobileNumber(), otp);

        return TokenResponse.builder()
                .success(true)
                .message("OTP sent successfully")
                .otp(otp)
                .build();
    }


    // ====================== LOGIN (SEND OTP IN EMAIL) ======================
    @Override
    public TokenResponse loginWithEmail(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthException("User not found with this email"));

        // Generate email OTP
        String otp = otpService.generateOtpForEmail(request.getEmail());

        log.info("OTP sent to email {}: {}", request.getEmail(), otp);

        return TokenResponse.builder()
                .success(true)
                .message("OTP sent successfully to email")
                .otp(otp)    // send OTP only for dev mode
                .build();
    }






    // ====================== VERIFY OTP ======================
    @Override
    @Transactional
    public TokenResponse verifyOtp(VerifyOtpRequest request) {
        String identifier = request.getIdentifier();
        String otpCode = request.getOtp();
        verifyMobileOtp(identifier, otpCode);

        User user = userRepository.findByMobileNumber(identifier)
                .orElseThrow(() -> new AuthException("User not found with mobile: " + identifier));

        String token = generateToken(user,"");
        return TokenResponse.builder()
                .success(true)
                .message("Login successful")
                .userId(user.getId())
                .roles(user.getRoles())
                .name(user.getName())
                .email(user.getEmail())
                .mobileNumber(user.getMobileNumber())
                .token(token)
                .build();
    }
    @Override
    public TokenResponse loginByPassword(LoginRequestByPasswordDTO request) {

        User user = userRepository.findByName(request.getName())
                .orElseThrow(() -> new BadRequestException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid username or password");
        }

        String token = jwtUtil.generateToken(user, "");

        return TokenResponse.builder()
                .success(true)
                .message("Login successful")
                .token(token)
                .roles(user.getRoles())
                .name(user.getName())
                .email(user.getEmail())
                .mobileNumber(user.getMobileNumber())
                .userId(user.getId())
                .build();
    }

    // ====================== PRIVATE HELPER ======================
    private void verifyMobileOtp(String mobileNumber, String otpCode) {
        log.info("Verifying mobile OTP for mobile: {}, code: {}", mobileNumber, otpCode);

        boolean isValid = otpService.verifyOtp(mobileNumber, otpCode);
        if (!isValid) {
            log.error("Invalid OTP for mobile: {}", mobileNumber);
            throw new AuthException("Invalid OTP code for mobile: " + mobileNumber);
        }

        User user = userRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new AuthException("User not found with mobile: " + mobileNumber));

        user.setMobileVerified(true);
        userRepository.save(user);
        log.info("User mobile verified successfully: {}", user.getId());
    }



    private String generateToken(User user, String ipAddress) {
        // Determine token expiration time based on role
        long expirationMillis;
        boolean isUserOnly = user.getRoles().contains(Role.USER) && !user.getRoles().contains(Role.ADMIN);
        if (isUserOnly) {
            expirationMillis = 100L * 365 * 24 * 60 * 60 * 1000; // 100 years
        } else {
            expirationMillis = 7L * 24 * 60 * 60 * 1000; // 7 days
        }
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(expirationMillis/ 1000);
        // Provide default values ipAddress if they are null
        String finalIpAddress = (ipAddress != null && !ipAddress.isEmpty()) ? ipAddress : "0.0.0.0";
        UserSession session = UserSession.builder().sessionId(UUID.randomUUID().toString()).user(user)
                .ipAddress(finalIpAddress).active(true).expiresAt(expiresAt).build();
        userSessionRepository.save(session);
        return jwtUtil.generateToken(user, session.getSessionId());
    }
}
