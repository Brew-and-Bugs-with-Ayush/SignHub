package com.ayush.signhub.util;


import java.security.SecureRandom;

public class OtpGenerator {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public static String generateOtp() {
        int otp = SECURE_RANDOM.nextInt(1_000_000); // 0 - 999999
        return String.format("%06d", otp);
    }
}
