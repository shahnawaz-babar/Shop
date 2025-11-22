package com.shop.smartshop.schedular;

import com.shop.smartshop.repository.OtpRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpSchedularService {


    public final OtpRepository otpRepository;

    // Run Every 5 Minutes
    @Scheduled(fixedRate = 3000000)
    public void deleteOtpExpire()
    {
        LocalDateTime time= LocalDateTime.now();
        otpRepository.deleteByExpiresAt(time);
        log.info("✅ Old OTPs deleted at " +time);
    }

}
