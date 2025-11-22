package com.shop.smartshop.util;

import com.shop.smartshop.entity.User;
import com.shop.smartshop.enums.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtUtil {


    // Constants for token expiration times
    private static final long NON_EXPIRING_TOKEN_DURATION = 100L * 365 * 24 * 60 * 60 * 1000; // 100 years in milliseconds
    private static final long STANDARD_TOKEN_DURATION = 7L * 24 * 60 * 60 * 1000; // 7 days in milliseconds

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expirationMs;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(User user, String sessionId) {
        log.debug("Generating JWT token for user: {}", user.getId());

        List<String> roles = user.getRoles().stream()
                .map(Role::name)
                .collect(Collectors.toList());

        // Determine expiration time based on roles
        long expirationTime = calculateExpirationTime(user.getRoles());

        String token = Jwts.builder()
                .setSubject(user.getEmail()) // or getUsername()
                .claim("roles", roles)
                .claim("sessionId",sessionId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .setIssuer("Smart_Shop")
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();

        log.debug("JWT token generated successfully with expiration: {} ms", expirationTime);
        return token;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT: {}", e.getMessage());
        }
        return false;
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }


    public String extractRole(String token) {
        return (String) extractAllClaims(token).get("role");
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private long calculateExpirationTime(Set<Role> roles) {
        // If user has only the USER role and no other roles, use non-expiring token
        if (roles.size() == 1 && roles.contains(Role.USER)) {
            log.debug("Using non-expiring token for ROLE_USER");
            return NON_EXPIRING_TOKEN_DURATION;
        }

        // For any other role combination, use standard token duration
        log.debug("Using standard token duration for roles: {}", roles);
        return STANDARD_TOKEN_DURATION;
    }

}
