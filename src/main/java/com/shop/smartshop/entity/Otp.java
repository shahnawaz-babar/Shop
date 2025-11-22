package com.shop.smartshop.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "otps")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Otp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String identifier;

    @Column(nullable = false)
    private String mobileNumber;

    @Column
    private String email;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private boolean verified;

    @Column(nullable = false)
    private boolean blocked;

    @Column
    private LocalDateTime verifiedAt;

    @Column
    private LocalDateTime expiresAt;


}