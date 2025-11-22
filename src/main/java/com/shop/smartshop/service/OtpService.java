package com.shop.smartshop.service;

public interface OtpService {

    /**
     * Generates a new OTP for the given identifier (e.g., mobile number or email).
     *
     * @param identifier the mobile number or unique user identifier
     * @return the generated OTP code
     */
    String generateOtp(String identifier);

    /**
     * Verifies an OTP for a given identifier.
     *
     * @param identifier the mobile number or unique identifier
     * @param code the OTP code entered by user
     * @return true if OTP is valid and verified successfully, false otherwise
     */
    boolean verifyOtp(String identifier, String code);
}
