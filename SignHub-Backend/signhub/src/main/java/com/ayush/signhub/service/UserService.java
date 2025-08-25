package com.ayush.signhub.service;

import com.ayush.signhub.domain.dto.UserRequestDto;
import com.ayush.signhub.domain.dto.UserResponseDto;
import com.ayush.signhub.domain.entity.User;
import com.ayush.signhub.mapper.UserMapper;
import com.ayush.signhub.repository.UserRepo;
import com.ayush.signhub.util.OtpGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    public List<UserResponseDto> getAllUsers() {
        List<User> allUsers = userRepo.findAll();
        return allUsers.stream()
                .map(UserMapper::toResponseDto)
                .toList();
    }

    public UserResponseDto getUser(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found" + email));

        return UserMapper.toResponseDto(user);
    }

    @Transactional
    public UserResponseDto createUser(UserRequestDto userRequestDto){
        User entity = UserMapper.toEntity(userRequestDto);

        if (userRepo.existsByEmail(userRequestDto.email())){
            throw new ResponseStatusException(HttpStatus.CONFLICT ,"Email already exists");
        }

        User save = userRepo.save(entity);
        save.setPassword(passwordEncoder.encode(entity.getPassword()));

        return UserMapper.toResponseDto(save);
    }

    @Transactional
    public void sendResetOtp(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with: " + email));

        String otp = OtpGenerator.generateOtp();

        long expiry = Instant.now().plusSeconds(300).toEpochMilli(); // 5 minutes

        user.setResetOtp(otp);
        user.setResetOtpExpiresAt(expiry);

        userRepo.save(user);

        // 5. Send email
        mailService.sendMail(
                user.getEmail(),
                "Password Reset OTP",
                "Hello " + user.getName() + ",\n\n" +
                        "Your password reset OTP is: " + otp + "\n" +
                        "This OTP will expire in 5 minutes.\n\n" +
                        "If you didn’t request a reset, please ignore this email.\n\n" +
                        "Best regards,\n SignHub"
        );

        log.info("Generated OTP for {} = {}", email, otp);
    }

    @Transactional
    public String verifyResetOtp(String email, String otp, String newPassword) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with: " + email));

        if (user.getResetOtp() == null || user.getResetOtpExpiresAt() == null) {
            return "No OTP found. Please request a new one.";
        }

        long now = Instant.now().toEpochMilli();

        // Normalizing input (handles leading zeros)
        String normalizedOtp;
        try {
            normalizedOtp = String.format("%06d", Integer.parseInt(otp));
        } catch (NumberFormatException e) {
            return "Invalid OTP format.";
        }

        if (!normalizedOtp.equals(user.getResetOtp())) {
            return "Invalid OTP.";
        }

        if (now > user.getResetOtpExpiresAt()) {
            return "OTP expired. Please request a new one.";
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetOtp(null);
        user.setResetOtpExpiresAt(0L);
        userRepo.save(user);

        return "Password reset successful.";
    }
    @Transactional
    public void sendEmailOtp(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with: " + email));

        if (user.isAccountVerified()) {
            throw new IllegalStateException("Account already verified.");
        }

        String otp = OtpGenerator.generateOtp();
        long expiry = Instant.now().plusSeconds(300).toEpochMilli();

        user.setVerifyOtp(otp);
        user.setVerifyOtpExpiresAt(expiry);
        userRepo.save(user);

        mailService.sendMail(user.getEmail(), "Email Verification OTP",
                "Your verification OTP is: " + otp + "\nThis OTP is valid for 5 minutes.");
    }

    @Transactional
    public String verifyEmailOtp(String email, String otp) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with: " + email));

        if (user.isAccountVerified()) {
            return "Account already verified.";
        }

        if (user.getVerifyOtp() == null || user.getVerifyOtpExpiresAt() == null) {
            return "No OTP found. Please request a new one.";
        }

        long now = Instant.now().toEpochMilli();

        // Normalize input to handle leading zeros safely
        String normalizedOtp;
        try {
            normalizedOtp = String.format("%06d", Integer.parseInt(otp));
        } catch (NumberFormatException e) {
            return "Invalid OTP format.";
        }

        if (!normalizedOtp.equals(user.getVerifyOtp())) {
            return "Invalid OTP.";
        }

        if (now > user.getVerifyOtpExpiresAt()) {
            return "OTP expired. Please request a new one.";
        }

        user.setAccountVerified(true);
        user.setVerifyOtp(null);
        user.setVerifyOtpExpiresAt(0L);
        userRepo.save(user);

        return "Email verified successfully.";
    }


}

