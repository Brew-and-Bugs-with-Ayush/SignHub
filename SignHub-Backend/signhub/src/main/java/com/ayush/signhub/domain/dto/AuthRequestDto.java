package com.ayush.signhub.domain.dto;

public record AuthRequestDto(
        String email,
        String password
) {
}
