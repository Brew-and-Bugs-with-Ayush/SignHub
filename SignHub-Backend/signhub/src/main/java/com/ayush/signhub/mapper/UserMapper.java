package com.ayush.signhub.mapper;

import com.ayush.signhub.domain.dto.UserRequestDto;
import com.ayush.signhub.domain.dto.UserResponseDto;
import com.ayush.signhub.domain.entity.User;


public class UserMapper {

    public static User toEntity(UserRequestDto dto) {
        return User.builder()
                .name(dto.name())
                .email(dto.email())
                .password(dto.password())
                .build();
    }

    public static UserResponseDto toResponseDto(User user) {
        return new UserResponseDto(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.isAccountVerified()
        );
    }
}
