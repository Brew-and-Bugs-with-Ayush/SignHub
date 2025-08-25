package com.ayush.signhub.exceptionhandling;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String errorCode = (String) request.getAttribute("jwt_error");

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        if (errorCode == null) {
            errorCode = "AUTHENTICATION_FAILED";
        }

        String json = String.format("{\"error\": \"%s\", \"message\": \"%s\"}",
                errorCode, authException.getMessage());

        response.getWriter().write(json);
    }
}
