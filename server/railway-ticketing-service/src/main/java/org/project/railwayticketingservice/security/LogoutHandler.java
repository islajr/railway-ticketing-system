package org.project.railwayticketingservice.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.project.railwayticketingservice.repository.RefreshTokenRepository;
import org.project.railwayticketingservice.service.CustomUserDetailsService;
import org.project.railwayticketingservice.service.JwtService;
import org.project.railwayticketingservice.service.TokenService;
import org.project.railwayticketingservice.util.CookieUtils;
import org.project.railwayticketingservice.util.Utilities;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class LogoutHandler implements org.springframework.security.web.authentication.logout.LogoutHandler {

    private final TokenService tokenService;
    private final CookieUtils cookieUtils;
    private final Utilities utilities;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;

    @SneakyThrows
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        final String authHeader = request.getHeader("Authorization");
        final String AUTH_PREFIX = "Bearer ";

        if (authHeader != null && authHeader.startsWith(AUTH_PREFIX)) {
            String token = authHeader.substring(AUTH_PREFIX.length());
            if (tokenService.isTokenAllowed(token)) {
                tokenService.disallowToken(token);
                log.info("token disallowed for e-mail {}", jwtService.extractEmail(token));
            } else {
                utilities.handleException(response, request, HttpServletResponse.SC_UNAUTHORIZED, "token is disallowed!");
            }
            // invalidate refresh token
            Cookie clearRefreshCookie;
            String email = jwtService.extractEmail(token);
            if (customUserDetailsService.loadUserByUsername(email).getAuthorities().equals(Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN")))) {
                log.info("clearing admin cookies for e-mail: {}", email);
                clearRefreshCookie = cookieUtils.clearRefreshTokenCookie("admin");
            } else {    // if it's a passenger
                log.info("clearing cookies for passenger e-mail: {}", email);
                clearRefreshCookie = cookieUtils.clearRefreshTokenCookie("passenger");
            }
            refreshTokenRepository.deleteRefreshTokenByEmail(email);
            response.addCookie(clearRefreshCookie);
        } else {
            utilities.handleException(response, request, HttpServletResponse.SC_BAD_REQUEST, "failed to logout!");
        }

        // invalidate session
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
            log.info("Session invalidated for user: {}", authentication.getName());
        }

        // clear security context
        SecurityContextHolder.clearContext();
        log.info("Logout successful");

    }
}
