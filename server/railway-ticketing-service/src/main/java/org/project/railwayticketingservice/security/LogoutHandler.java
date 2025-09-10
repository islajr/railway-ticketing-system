package org.project.railwayticketingservice.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.project.railwayticketingservice.exception.exceptions.RtsException;
import org.project.railwayticketingservice.service.TokenService;
import org.project.railwayticketingservice.util.CookieUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LogoutHandler implements org.springframework.security.web.authentication.logout.LogoutHandler {

    private final TokenService tokenService;
    private final CookieUtils cookieUtils;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        final String authHeader = request.getHeader("Authorization");
        final String AUTH_PREFIX = "Bearer ";

        if (authHeader != null && authHeader.startsWith(AUTH_PREFIX)) {
            String token = authHeader.substring(AUTH_PREFIX.length());
            if (tokenService.isTokenAllowed(token)) {
                tokenService.disallowToken(token);
                System.out.println("token disallowed for user " + authentication.getName());
            } else {
                throw new RtsException(HttpStatus.UNAUTHORIZED, "token is disallowed!");
            }
            // invalidate refresh token
            Cookie clearRefreshCookie = cookieUtils.clearRefreshTokenCookie();
            response.addCookie(clearRefreshCookie);
        } else {
            throw new RtsException(HttpStatus.BAD_REQUEST, "failed to logout!");
        }

        // invalidate session
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
            System.out.println("session invalidated for user " + authentication.getName());
        }

        // clear security context
        SecurityContextHolder.clearContext();
        System.out.println("logout successful");

    }
}
