package com.ayush.signhub.controller;

import com.ayush.signhub.domain.dto.AuthRequestDto;
import com.ayush.signhub.domain.dto.AuthResponseDto;
import com.ayush.signhub.jwt.JwtService;
import com.ayush.signhub.service.AuthService;
import com.ayush.signhub.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;
    private final MailService mailService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> loginUser(@RequestBody AuthRequestDto requestDto) {
        UserDetails userDetails = authService.loginUser(requestDto.email(), requestDto.password());

        String tokenValue = jwtService.generateToken(userDetails);

        AuthResponseDto build = AuthResponseDto.builder()
                .token(tokenValue)
                .expiresIn(3600000)
                .build();

        ResponseCookie cookie = ResponseCookie.from("jwt" , tokenValue)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(3600)
                .sameSite("strict")
                .build();

        mailService.sendMail(
                requestDto.email(),
                "Login Alert",
                "Hello " + requestDto.email() + ",\n\n" +
                        "You have successfully logged in at " + LocalDateTime.now() + ".\n" +
                        "If this wasn’t you, please reset your password immediately.\n\n" +
                        "Best regards,\n SignHub"
        );

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE , cookie.toString())
                .body(build);
    }

    @GetMapping("/is-authenticated")
    public ResponseEntity<Boolean> isAuthenticated(@CurrentSecurityContext(expression = "authentication?.name")
                                                   String email){
        return ResponseEntity.ok(email !=null);
    }
}
