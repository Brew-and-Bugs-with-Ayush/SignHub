package com.ayush.signhub.domain.dto;

import lombok.Builder;

@Builder
public record AuthResponseDto(
         String token,
         long expiresIn
) {
}
