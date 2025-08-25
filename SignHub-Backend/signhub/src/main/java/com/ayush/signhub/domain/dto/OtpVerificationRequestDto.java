package com.ayush.signhub.domain.dto;

import jakarta.validation.constraints.NotBlank;

public record OtpVerificationRequestDto(

        @NotBlank(message = "Email is required")
        String email,
        @NotBlank(message = "password is required")
        String newPassword,
        @NotBlank(message = "otp is required")
        String otp
) {
}
