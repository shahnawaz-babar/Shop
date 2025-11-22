package com.shop.smartshop.security;

import com.shop.smartshop.entity.User;
import com.shop.smartshop.enums.Role;
import com.shop.smartshop.repository.UserRepository;
import com.shop.smartshop.util.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class GoogleOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        log.info("Google OAuth2 Success: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .name(name)
                            .email(email)
                            .mobileVerified(true)
                            .roles(Set.of(Role.USER))
                            .build();
                    return userRepository.save(newUser);
                });

        String token = jwtUtil.generateToken(user, "google-oauth2");

        // Redirect to frontend with token
        String frontendUrl = "http://localhost:3000/oauth2/success?token=" + token;
        response.sendRedirect(frontendUrl);
    }
}

