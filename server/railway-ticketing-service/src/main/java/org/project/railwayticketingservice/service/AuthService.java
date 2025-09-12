package org.project.railwayticketingservice.service;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.InvalidKeyException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.project.railwayticketingservice.dto.auth.request.LoginAdminRequest;
import org.project.railwayticketingservice.dto.auth.request.LoginPassengerRequest;
import org.project.railwayticketingservice.dto.auth.request.RegisterAdminRequest;
import org.project.railwayticketingservice.dto.auth.request.RegisterPassengerRequest;
import org.project.railwayticketingservice.dto.auth.response.LoginAdminResponse;
import org.project.railwayticketingservice.dto.auth.response.LoginPassengerResponse;
import org.project.railwayticketingservice.dto.auth.response.RegisterAdminResponse;
import org.project.railwayticketingservice.dto.auth.response.RegisterPassengerResponse;
import org.project.railwayticketingservice.entity.*;
import org.project.railwayticketingservice.exception.exceptions.RtsException;
import org.project.railwayticketingservice.repository.AdminRepository;
import org.project.railwayticketingservice.repository.PassengerRepository;
import org.project.railwayticketingservice.repository.RefreshTokenRepository;
import org.project.railwayticketingservice.util.CookieUtils;
import org.project.railwayticketingservice.util.Utilities;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PassengerRepository passengerRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CookieUtils cookieUtils;
    private final RefreshTokenRepository refreshTokenRepository;
    private final Utilities utilities;
    private final CustomUserDetailsService customUserDetailsService;

    @Value("${security.jwt.refresh.expiration}")
    int refreshExpiration;

    @Value("${security.jwt.expiration}")
    int accessTokenExpiration;

    public ResponseEntity<RegisterPassengerResponse> registerPassenger(RegisterPassengerRequest request) {

        if (adminRepository.existsByEmail(request.email()) || passengerRepository.existsByEmail(request.email())) {
            throw new RtsException(HttpStatus.CONFLICT, "Email already in use");

        }

        Passenger passenger = request.toPassenger();
        passenger.setPassword(passwordEncoder.encode(passenger.getPassword()));
        passengerRepository.save(passenger);

        return ResponseEntity.status(HttpStatus.CREATED).build();

    }

    public ResponseEntity<LoginPassengerResponse> loginPassenger(LoginPassengerRequest request, HttpServletResponse response) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        Passenger passenger = passengerRepository.findPassengerByEmail(request.email());

        if (passenger != null) {

            if (authentication.isAuthenticated()) {
                String email = passenger.getEmail();
                String accessToken;
                String refreshToken;
                try {
                    accessToken = jwtService.generateToken(email);
                    refreshToken = jwtService.generateRefreshToken(email);
                } catch (InvalidKeyException ex) {
                    throw new RtsException(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid key");
                } catch (JwtException ex) {
                    throw new RtsException(HttpStatus.UNAUTHORIZED, "Invalid token");
                }

                // persisting refresh token
                RefreshToken storedToken = refreshTokenRepository.findRefreshTokenByEmail(email);
                if (storedToken == null) {
                    refreshTokenRepository.save(RefreshToken.builder()
                            .token(refreshToken)
                            .email(email)
                            .build());
                } else {
                    storedToken.setToken(refreshToken);
                    refreshTokenRepository.save(storedToken);
                }

                // refresh cookie setup
                Cookie refreshCookie = cookieUtils.createRefreshTokenCookie(refreshToken, (refreshExpiration / 1000), "passenger");
                response.addCookie(refreshCookie);
                return ResponseEntity.ok(LoginPassengerResponse.of(accessToken, accessTokenExpiration / 1000 + "s"));
            }
        }

        throw new RtsException(HttpStatus.BAD_REQUEST, "Invalid email or password");
    }

    public ResponseEntity<RegisterAdminResponse> registerAdmin(RegisterAdminRequest request) {

        if (adminRepository.existsByEmail(request.email()) || passengerRepository.existsByEmail(request.email())) {
            throw new RtsException(HttpStatus.CONFLICT, "Email already in use");
        }

        Admin admin = request.toAdmin();
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        adminRepository.save(admin);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    public ResponseEntity<LoginAdminResponse> loginAdmin(LoginAdminRequest request, HttpServletResponse response) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        Admin admin = adminRepository.findAdminByEmail(request.email());

        if (admin != null) {

            if (authentication.isAuthenticated()) {
                String email = admin.getEmail();
                String accessToken;
                String refreshToken;

                try {
                    accessToken = jwtService.generateToken(email);
                    refreshToken = jwtService.generateRefreshToken(email);
                } catch (InvalidKeyException ex) {
                    throw new RtsException(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid key");
                } catch (JwtException ex) {
                    throw new RtsException(HttpStatus.UNAUTHORIZED, "Invalid token");
                }

                // persisting refresh token
                RefreshToken storedToken = refreshTokenRepository.findRefreshTokenByEmail(email);
                if (storedToken == null) {
                    refreshTokenRepository.save(RefreshToken.builder()
                            .token(refreshToken)
                            .email(email)
                            .build());
                } else {
                    storedToken.setToken(refreshToken);
                    refreshTokenRepository.save(storedToken);
                }

                // refresh cookie setup
                Cookie refreshCookie = cookieUtils.createRefreshTokenCookie(refreshToken, (refreshExpiration / 1000), "admin");
                response.addCookie(refreshCookie);
                return ResponseEntity.ok(LoginAdminResponse.of(accessToken, accessTokenExpiration / 1000 + "s"));
            }
        }

        throw new RtsException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
    }

    public ResponseEntity<LoginAdminResponse> refreshAdminToken(String refreshToken, HttpServletResponse response) {
        RefreshToken storedToken = refreshTokenRepository.findRefreshTokenByToken(refreshToken).orElseThrow(
                () -> new RtsException(HttpStatus.BAD_REQUEST, "Invalid refresh token")
        );

        // e-mail confirmation
        String refreshEmail;
        try {
            refreshEmail = jwtService.extractEmail(refreshToken);
        } catch (JwtException e) {
            throw new RtsException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }

        if (refreshEmail.equals(storedToken.getEmail())) {
            throw new RtsException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }

        try {
            AdminPrincipal adminPrincipal = (AdminPrincipal) customUserDetailsService.loadUserByUsername(storedToken.getEmail());
            jwtService.verifyToken(refreshEmail, adminPrincipal);
        } catch (JwtException e) {
            throw new RtsException(HttpStatus.UNAUTHORIZED, "Expired refresh token");
        } catch (Exception ex) {    // should get the specific exception name -- InputMismatch?
            throw new RtsException(HttpStatus.BAD_REQUEST, "There was a problem validating the refresh token");
        }

        // rotate the token
        String newAccessToken = jwtService.generateToken(storedToken.getEmail());
        return ResponseEntity.ok(LoginAdminResponse.of(newAccessToken, accessTokenExpiration / 1000 + "s"));
    }

    public ResponseEntity<LoginPassengerResponse> refreshPassengerToken(String refreshToken, HttpServletResponse response) {
        RefreshToken storedToken = refreshTokenRepository.findRefreshTokenByToken(refreshToken).orElseThrow(
                () -> new RtsException(HttpStatus.BAD_REQUEST, "Invalid refresh token")
        );

        // e-mail confirmation
        String refreshEmail;
        try {
            refreshEmail = jwtService.extractEmail(refreshToken);
        } catch (JwtException e) {
            throw new RtsException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }

        if (refreshEmail.equals(storedToken.getEmail())) {
            throw new RtsException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }

        try {
            PassengerPrincipal passengerPrincipal = (PassengerPrincipal) customUserDetailsService.loadUserByUsername(storedToken.getEmail());
            jwtService.verifyToken(refreshEmail, passengerPrincipal);
        } catch (JwtException e) {
            throw new RtsException(HttpStatus.UNAUTHORIZED, "Expired refresh token");
        } catch (Exception ex) {    // should get the specific exception name -- InputMismatch?
            throw new RtsException(HttpStatus.BAD_REQUEST, "There was a problem validating the refresh token");
        }

        // rotate the token
        String newAccessToken = jwtService.generateToken(storedToken.getEmail());
        return ResponseEntity.ok(LoginPassengerResponse.of(newAccessToken, accessTokenExpiration / 1000 + "s"));
    }
}
