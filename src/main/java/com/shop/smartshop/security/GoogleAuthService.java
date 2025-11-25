package com.shop.smartshop.security;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.shop.smartshop.dto.ApiResponse;
import com.shop.smartshop.entity.User;
import com.shop.smartshop.enums.Role;
import com.shop.smartshop.exception.AuthException;
import com.shop.smartshop.repository.UserRepository;
import com.shop.smartshop.util.JwtUtil;
import com.shop.smartshop.verifier.GoogleTokenVerifier;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class GoogleAuthService {

    private final GoogleTokenVerifier googleTokenVerifier;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public ApiResponse<?> loginWithGoogle(String idToken) {

        GoogleIdToken.Payload payload = googleTokenVerifier.verify(idToken);

        if (payload == null) {
            throw new AuthException("Invalid Google token");
        }

        String email = payload.getEmail();
        String name = (String) payload.get("name");

        User user = userRepository.findByEmail(email)
                .orElseGet(() ->
                        userRepository.save(
                                User.builder()
                                        .name(name)
                                        .email(email)
                                        .mobileVerified(true)
                                        .roles(Set.of(Role.USER))
                                        .build()
                        )
                );

        String token = jwtUtil.generateToken(user, "google-oauth");

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("token", token);
        responseData.put("userId", user.getId());
        responseData.put("name", user.getName());
        responseData.put("email", user.getEmail());
        responseData.put("roles", user.getRoles());

        return ApiResponse.success("Login successful", responseData);
    }
}
