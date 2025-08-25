package com.ayush.signhub.controller;

import com.ayush.signhub.domain.dto.*;
import com.ayush.signhub.service.MailService;
import com.ayush.signhub.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final MailService mailService;

    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<UserResponseDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/user")
    public ResponseEntity<UserResponseDto> getUser(){

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        UserResponseDto user = userService.getUser(email);
        return new ResponseEntity<>(user , HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> createUser(
            @Valid
            @RequestBody UserRequestDto requestDto){

            UserResponseDto user = userService.createUser(requestDto);

        mailService.sendMail(
                user.email(),
                "Welcome to SignHub!",
                "Hi " + user.email() + ",\n\n" +
                        "Thank you for registering with us. We're excited to have you onboard!\n\n" +
                        "Best regards,\n SignHub"
        );
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PostMapping("/send-reset-otp")
    public ResponseEntity<SendResetOtpResponseDto> sendResetOtp(@RequestParam String email) {
        try {
            userService.sendResetOtp(email);
            return ResponseEntity.ok(new SendResetOtpResponseDto
                    ("OTP has been sent to your email.", true));
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(new SendResetOtpResponseDto
                    ("Failed to send OTP: " + e.getMessage(), false));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<SendResetOtpResponseDto> verifyResetOtp(
            @Valid
            @RequestBody OtpVerificationRequestDto request) {
        String result = userService.verifyResetOtp(request.email(), request.otp(), request.newPassword());

        boolean success = result.equals("Password reset successful.");
        return ResponseEntity.ok(new SendResetOtpResponseDto(result, success));
    }

    @PostMapping("/send-otp")
    public ResponseEntity<SendResetOtpResponseDto> sendVerificationOtp() {
        try {
           String email = SecurityContextHolder.getContext().getAuthentication().getName();

            userService.sendEmailOtp(email);

            return ResponseEntity.ok(new SendResetOtpResponseDto
                    ("Verification OTP sent.", true));

        }
        catch (IllegalStateException e) {
            return ResponseEntity.ok(new SendResetOtpResponseDto
                    ("Account is already verified.", false));
        }
        catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new SendResetOtpResponseDto
                            ("Failed: " + e.getMessage(), false));
        }
    }

    @PostMapping("/verify-email-otp")
    public ResponseEntity<VerifyEmailOtpResponseDto> verifyEmailOtp(@RequestBody VerifyEmailOtpRequestDto request) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();

            String result = userService.verifyEmailOtp(email, request.otp());

            boolean success = result.equalsIgnoreCase("Email verified successfully.");

            return ResponseEntity.ok(
                    new VerifyEmailOtpResponseDto(result, success)
            );
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new VerifyEmailOtpResponseDto
                            ("Failed: " + e.getMessage(), false)
            );
        }
    }

}
