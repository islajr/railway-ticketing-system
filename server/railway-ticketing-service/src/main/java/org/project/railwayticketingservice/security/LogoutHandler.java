package org.project.railwayticketingservice.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.project.railwayticketingservice.exception.exceptions.RtsException;
import org.project.railwayticketingservice.service.TokenService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LogoutHandler implements org.springframework.security.web.authentication.logout.LogoutHandler {

    private final TokenService tokenService;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        final String authHeader = request.getHeader("Authorization");
        final String AUTH_PREFIX = "Bearer ";

        if (authHeader != null && authHeader.startsWith(AUTH_PREFIX)) {
            String token = authHeader.substring(AUTH_PREFIX.length());
            if (tokenService.isTokenAllowed(token)) {
                tokenService.disallowToken(token);
            } else {
                throw new RtsException(HttpStatus.UNAUTHORIZED, "token is disallowed!");
            }
            // invalidate refresh token
        } else {
            throw new RuntimeException("failed to logout!");
        }

        // invalidate session
        HttpSession session = request.getSession(false);
        if (session != null)
            session.invalidate();

        // clear security context
        SecurityContextHolder.clearContext();

    }
}
