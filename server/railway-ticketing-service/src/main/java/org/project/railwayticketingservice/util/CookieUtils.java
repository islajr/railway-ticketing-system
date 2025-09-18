package org.project.railwayticketingservice.util;

import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CookieUtils {

    @Value("${global.client.url}")
    String clientURL;

    public Cookie createRefreshTokenCookie(String token, int maxAgeSeconds, String role) {
        Cookie cookie = new Cookie("refreshToken", token);
        cookie.setHttpOnly(true);   // transmitted only over https
        if (role.equals("admin")) {
            cookie.setPath("/api/v1/auth/admin/refresh"); // sent only to admin refresh endpoint
        } else if (role.equals("passenger")) {
            cookie.setPath("/api/v1/auth/passenger/refresh");   // sent only to passenger refresh endpoint
        } else {
            cookie.setPath("/api/v1/auth/refresh");
        }
        cookie.setMaxAge(maxAgeSeconds);
        cookie.setDomain(clientURL);  // should be set to actual client prod domain
        cookie.setAttribute("SameSite", "Strict");
        log.info("Refresh Cookie successfully created");
        return cookie;
    }

    public Cookie clearRefreshTokenCookie(String role) {
        Cookie clearedCookie = new Cookie("refreshToken", null);
        clearedCookie.setHttpOnly(true);
        if (role.equals("admin")) {
            clearedCookie.setPath("/api/v1/auth/admin/refresh");
        } else if (role.equals("passenger")) {
            clearedCookie.setPath("/api/v1/auth/passenger/refresh");
        } else {
            clearedCookie.setPath("/api/v1/auth/refresh");
        }
        clearedCookie.setMaxAge(0); // expires immediately
        clearedCookie.setDomain(clientURL);
        clearedCookie.setAttribute("SameSite", "Strict");
        log.info("Refresh Cookie successfully cleared");
        return clearedCookie;
    }
}
