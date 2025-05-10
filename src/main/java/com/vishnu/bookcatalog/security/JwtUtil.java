package com.vishnu.bookcatalog.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@Slf4j
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.otpExpirationMinutes}")
    private long otpExpirationMinutes;

    @Value("${jwt.authExpirationMinutes}")
    private long authExpirationMinutes;

    private SecretKey key;

    @PostConstruct
    public void init() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("JWT secret key must be at least 32 characters long");
        }
        key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateOtpToken(String username) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + otpExpirationMinutes * 60_000);
        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiry)
                .claim("otpPending", true)
                .signWith(key)
                .compact();
    }

    public String validateOtpTokenAndGetUsername(String token) {
        try {
            final Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            Boolean pending = claims.get("otpPending", Boolean.class);
            if (pending == null || !pending) {
                throw new IllegalArgumentException("OTP token is not pending");
            }
            return claims.getSubject();
        } catch (JwtException | IllegalArgumentException ex) {
            log.warn("Invalid or expired OTP token", ex);
            throw new IllegalArgumentException("Invalid or expired OTP token");
        }
    }

    public String generateAuthToken(String username) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + authExpirationMinutes * 60_000);
        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    public String validateAuthTokenAndGetUsername(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            Boolean pending = claims.get("otpPending", Boolean.class);
            if (pending != null && pending) {
                throw new IllegalArgumentException("OTP token is not valid for full API access");
            }
            return claims.getSubject();
        } catch (JwtException | IllegalArgumentException ex) {
            log.warn("Invalid or expired auth token", ex);
            throw new IllegalArgumentException("Invalid or expired auth token");
        }
    }
}
