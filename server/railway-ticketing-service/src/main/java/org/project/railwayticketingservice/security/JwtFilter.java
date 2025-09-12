package org.project.railwayticketingservice.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.project.railwayticketingservice.entity.AdminPrincipal;
import org.project.railwayticketingservice.entity.PassengerPrincipal;
import org.project.railwayticketingservice.repository.AdminRepository;
import org.project.railwayticketingservice.repository.PassengerRepository;
import org.project.railwayticketingservice.service.CustomUserDetailsService;
import org.project.railwayticketingservice.service.JwtService;
import org.project.railwayticketingservice.service.TokenService;
import org.project.railwayticketingservice.util.Utilities;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtService jwtService;
    private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final PassengerRepository passengerRepository;
    private final Utilities utilities;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        final String AUTH_PREFIX = "Bearer ";
        String token = null;
        String email = null;

        if (authHeader != null && authHeader.startsWith(AUTH_PREFIX)) {
            token = authHeader.substring(AUTH_PREFIX.length());
            try {
                email = jwtService.extractEmail(token);
            } catch (ExpiredJwtException ex) {
                utilities.handleException(response, request, HttpServletResponse.SC_UNAUTHORIZED, "Expired JWT Token");
                return;
            } catch (JwtException ex) {
                utilities.handleException(response, request, HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT Token");
                return;
            }
        }

        // check if token has been disallowed
        if (!tokenService.isTokenAllowed(token)) {
            utilities.handleException(response, request, HttpServletResponse.SC_UNAUTHORIZED, "Blacklisted JWT Token");
            return;
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (passengerRepository.existsByEmail(email)) {

                PassengerPrincipal passengerPrincipal = (PassengerPrincipal) customUserDetailsService.loadUserByUsername(email);

                if (jwtService.verifyToken(token, passengerPrincipal)) {
                    UsernamePasswordAuthenticationToken authToken
                            = new UsernamePasswordAuthenticationToken(passengerPrincipal, null, passengerPrincipal.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    utilities.handleException(response, request, HttpServletResponse.SC_UNAUTHORIZED, "Failed to verify JWT Token");
                    return;
                }
            } else if (adminRepository.existsByEmail(email)) {
                AdminPrincipal adminPrincipal = (AdminPrincipal) customUserDetailsService.loadUserByUsername(email);

                if (jwtService.verifyToken(token, adminPrincipal)) {
                    UsernamePasswordAuthenticationToken authToken
                            = new UsernamePasswordAuthenticationToken(adminPrincipal, null, adminPrincipal.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    utilities.handleException(response, request, HttpServletResponse.SC_UNAUTHORIZED, "Failed to verify JWT Token");
                    return;
                }
            } else {
                utilities.handleException(response, request, HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT Token");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }


}
