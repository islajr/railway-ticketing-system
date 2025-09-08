package org.project.railwayticketingservice.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.project.railwayticketingservice.entity.AdminPrincipal;
import org.project.railwayticketingservice.entity.PassengerPrincipal;
import org.project.railwayticketingservice.exception.exceptions.RtsException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    private final String secret;

    @Value("${security.jwt.expiration}")
    long expiration;    // six hours

    @Value("${security.jwt.refresh.expiration}")
    long refreshExpiration;     // three days

    public JwtService() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
            SecretKey secretKey = keyGenerator.generateKey();
            secret = Base64.getEncoder().encodeToString(secretKey.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private SecretKey generateKey() {
        byte[] secretByte = Base64.getDecoder().decode(secret);
        return Keys.hmacShaKeyFor(secretByte);
    }

    public String generateToken(String email) {
        Map<String, Object> claims = new HashMap<>();

        return Jwts.builder()
                .subject(email)
                .claims(claims)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(generateKey())
                .compact();

    }

    public String generateRefreshToken(String email) {
        Map<String, Object> claims = new HashMap<>();

        return Jwts.builder()
                .subject(email)
                .claims(claims)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(generateKey())
                .compact();
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(generateKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

    }

    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    public boolean verifyToken(String token, PassengerPrincipal passengerPrincipal) {
        return (extractEmail(token).equals(passengerPrincipal.getEmail()) && !isTokenExpired(token));
    }

    public boolean verifyToken(String token, AdminPrincipal adminPrincipal) {
        return (extractEmail(token).equals(adminPrincipal.getEmail()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

}
