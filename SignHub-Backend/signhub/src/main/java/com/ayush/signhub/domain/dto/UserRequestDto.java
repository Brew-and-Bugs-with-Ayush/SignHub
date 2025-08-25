package com.ayush.signhub.domain.dto;

import jakarta.validation.constraints.*;

public record UserRequestDto(

        @NotBlank(message = "Name required it should not be empty")
        String name,

        @Email(message = "Enter a valid email address")
        @NotBlank(message ="Email required it should not be empty")
        String email,

        @Size(min = 6 , max = 10 , message = "Password must be least {min} characters and maximum {max} characters")
        @Pattern(regexp = "^[\\w\\s-]+$" , message = "Password can only contains letters , numbers , spaces and hyphens")
        String password
) {
}
