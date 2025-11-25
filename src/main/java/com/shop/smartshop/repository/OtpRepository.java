package com.shop.smartshop.repository;

import com.shop.smartshop.entity.Otp;
import com.shop.smartshop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<Otp,Long> {

    Optional<Otp> findByMobileNumberAndVerifiedFalseAndBlockedFalse(String mobileNumber);
    Optional<Otp> findByEmailAndVerifiedFalseAndBlockedFalse(String mobileNumber);
    void deleteByMobileNumberAndVerifiedFalseAndBlockedFalse(String mobileNumber);
    void deleteByExpiresAt(LocalDateTime expiresAt);


}
