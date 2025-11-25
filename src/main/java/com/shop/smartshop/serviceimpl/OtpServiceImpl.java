package com.shop.smartshop.serviceimpl;

import com.shop.smartshop.entity.Otp;
import com.shop.smartshop.repository.OtpRepository;
import com.shop.smartshop.service.OtpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {


    private final SecureRandom random = new SecureRandom();
    private final OtpRepository otpRepository;


    @Transactional
    public String generateOtp(String identifier) {

        Otp prevOtp=otpRepository.findByMobileNumberAndVerifiedFalseAndBlockedFalse(identifier).orElse(null);
        if(prevOtp!=null)
        {
            otpRepository.deleteByMobileNumberAndVerifiedFalseAndBlockedFalse(prevOtp.getMobileNumber());
            log.info("Otp for this number is Already Present {},Deleted",prevOtp.getMobileNumber());
            log.info("New Otp generated {}",prevOtp.getMobileNumber());
        }

        String code = generateRandomCode();
        // Create new OTP
        Otp otp = Otp.builder()
                .identifier(identifier)
                .code(code)
                .verified(false)
                .blocked(false)
                .expiresAt(LocalDateTime.now().plusMinutes(2))
                .build();
        otp.setMobileNumber(identifier);
        // Format the mobile number with +91 prefix if it doesn't have a country code
        String formattedMobile = identifier.startsWith("+") ? identifier : "+91" + identifier;
        // Use the standard message format required by Digital SMS API
        String message = "Your Smart Shop verification code is: " + code + ". Do not share this code with anyone. It is valid for 2 minutes.";
        otpRepository.save(otp);
        log.info("Generated OTP for {}: {}", identifier, code);
        return code;
    }


    @Transactional
    public boolean verifyOtp(String identifier, String code) {
        log.info("Verifying OTP for identifier: {}, code: {}, type: {}", identifier, code);

        // Normalize identifier (in case it's sent as a number)
        String normalized = identifier == null ? null : identifier.trim();

        // Get all OTPs for the identifier
        Optional<Otp> otpOptional =
                otpRepository.findByMobileNumberAndVerifiedFalseAndBlockedFalse(identifier) ;
        if (otpOptional.isEmpty()) {
            log.warn("No valid unverified OTPs found for {}", identifier);
            return false;
        }

        Otp otp = otpOptional.get();

        log.info("Found active OTP: id={}, code={}, verified={}, blocked={}, expiresAt={}",
                otp.getId(), otp.getCode(), otp.isVerified(),
                otp.isBlocked(), otp.getExpiresAt());

        // Check if OTP matches
        if (!otp.getCode().equals(code)) {
            log.warn("OTP code mismatch. Expected: {}, Actual: {}", otp.getCode(), code);
            return false;
        }

        // Check if OTP is expired
        if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
            log.warn("OTP has expired. Expires at: {}, Current time: {}",
                    otp.getExpiresAt(), LocalDateTime.now());
            return false;
        }

        // Mark as verified
        otp.setVerified(true);
        otp.setVerifiedAt(LocalDateTime.now());
        otpRepository.save(otp);

        log.info("OTP verified successfully for {}", identifier);

        return true;
    }

    @Override
    public String generateOtpForEmail(String identifier) {

        Otp prevOtp=otpRepository.findByEmailAndVerifiedFalseAndBlockedFalse(identifier).orElse(null);
        if(prevOtp!=null)
        {
            otpRepository.findByEmailAndVerifiedFalseAndBlockedFalse(prevOtp.getMobileNumber());
            log.info("Otp for this number is Already Present {},Deleted",prevOtp.getMobileNumber());
            log.info("New Otp generated {}",prevOtp.getMobileNumber());
        }

        String code = generateRandomCode();
        // Create new OTP
        Otp otp = Otp.builder()
                .identifier(identifier)
                .code(code)
                .verified(false)
                .blocked(false)
                .expiresAt(LocalDateTime.now().plusMinutes(2))
                .build();
        otp.setMobileNumber(identifier);
        // Format the mobile number with +91 prefix if it doesn't have a country code
        String formattedMobile = identifier.startsWith("+") ? identifier : "+91" + identifier;
        // Use the standard message format required by Digital SMS API
        String message = "Your Smart Shop verification code is: " + code + ". Do not share this code with anyone. It is valid for 2 minutes.";
        otpRepository.save(otp);
        log.info("Generated OTP for {}: {}", identifier, code);
        return code;
    }

    private String generateRandomCode() {
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

}
