package com.ayush.signhub.domain.dto;

public record UserResponseDto(
        String userId,
        String name,
        String email,
        boolean isAccountVerified
) {
}
