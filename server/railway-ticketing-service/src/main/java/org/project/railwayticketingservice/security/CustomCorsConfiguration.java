package org.project.railwayticketingservice.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

@Slf4j
@Component
public class CustomCorsConfiguration implements CorsConfigurationSource {

    @Value("${global.client.url}")
    String client;

    @Override
    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
        String origin = request.getHeader("Origin");
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        if (origin != null) {

            log.info("Allowing incoming CORS request from: {}", origin);

            corsConfiguration.setAllowCredentials(true);
            corsConfiguration.addAllowedOrigin(origin);
            corsConfiguration.setAllowedHeaders(List.of("*"));
            corsConfiguration.setAllowedMethods(List.of("POST", "GET", "PUT", "DELETE", "PATCH", "OPTIONS"));
        }

        return corsConfiguration;
    }
}
