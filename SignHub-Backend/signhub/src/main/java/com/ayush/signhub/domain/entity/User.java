package com.ayush.signhub.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(unique = true)
    private String userId;

    @Column(nullable = false)
    private String name;

    @Column(unique = true , nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String verifyOtp;
    private String resetOtp;

    @Builder.Default
    private boolean isAccountVerified = false;

    private Long verifyOtpExpiresAt;
    private Long resetOtpExpiresAt;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate(){
        if (this.userId == null) {
            this.userId = UUID.randomUUID().toString();
        }
        this.isAccountVerified = false; // default for new users


        // defaults for OTP fields
        this.verifyOtp = null;
        this.verifyOtpExpiresAt = 0L;
        this.resetOtp = null;
        this.resetOtpExpiresAt = 0L;
    }
}
