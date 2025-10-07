package org.example.iblab1.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        final Map<String, Object> body = new HashMap<>();
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("error", "Unauthorized");

        String errorMessage = getErrorMessage(request, authException);
        body.put("message", errorMessage);
        body.put("path", request.getServletPath());

        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), body);
    }

    private String getErrorMessage(HttpServletRequest request, AuthenticationException authException) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || authHeader.trim().isEmpty()) {
            return "JWT token is missing. Please provide Authorization header with Bearer token";
        }

        if (!authHeader.startsWith("Bearer ")) {
            return "Invalid Authorization header format. Expected: 'Bearer <token>'";
        }

        String jwtToken = authHeader.substring(7);
        if (jwtToken.trim().isEmpty()) {
            return "JWT token is empty";
        }

        return "Invalid or expired JWT token";
    }
}