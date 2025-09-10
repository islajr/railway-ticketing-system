package org.project.railwayticketingservice.util;

import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CookieUtils {

    @Value("${global.client.url}")
    String clientURL;

    public Cookie createRefreshTokenCookie(String token, int maxAgeSeconds) {
        Cookie cookie = new Cookie("refreshToken", token);
        cookie.setHttpOnly(true);   // transmitted only over https
        cookie.setPath("/api/v1/auth/refresh"); // sent only to refresh endpoint
        cookie.setMaxAge(maxAgeSeconds);
        cookie.setDomain(clientURL);  // should be set to actual client prod domain
        cookie.setAttribute("SameSite", "Strict");
        return cookie;
    }

    public Cookie clearRefreshTokenCookie() {
        Cookie clearedCookie = new Cookie("refreshToken", null);
        clearedCookie.setHttpOnly(true);
        clearedCookie.setPath("/api/v1/auth/refresh");
        clearedCookie.setMaxAge(0); // expires immediately
        clearedCookie.setDomain(clientURL);
        clearedCookie.setAttribute("SameSite", "Strict");
        return clearedCookie;
    }
}
