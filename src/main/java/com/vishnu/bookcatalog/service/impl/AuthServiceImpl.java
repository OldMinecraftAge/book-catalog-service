package com.vishnu.bookcatalog.service.impl;

import com.vishnu.bookcatalog.dto.LoginRequest;
import com.vishnu.bookcatalog.security.JwtUtil;
import com.vishnu.bookcatalog.service.AuthService;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authManager;
    private final GoogleAuthenticator googleAuthenticator;
    private final JwtUtil jwtUtil;
    private final String totpSecret;

    @Autowired
    public AuthServiceImpl(AuthenticationManager authManager,
                           GoogleAuthenticator googleAuthenticator,
                           JwtUtil jwtUtil,
                           @Value("${totp.secret}") String totpSecret) {
        this.authManager = authManager;
        this.googleAuthenticator = googleAuthenticator;
        this.jwtUtil = jwtUtil;
        this.totpSecret = totpSecret;
    }

    @Override
    public String authenticateCredentials(LoginRequest request) {
        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );
        } catch (AuthenticationException ex) {
            log.warn("Invalid credentials for user {}", request.username());
            throw ex;
        }
        String otpToken = jwtUtil.generateOtpToken(request.username());
        log.debug("Issued OTP token for user {}", request.username());
        return otpToken;
    }

    @Override
    public String verifyTotpAndIssueToken(String otpToken, int totpCode) {
        String username = jwtUtil.validateOtpTokenAndGetUsername(otpToken);
        log.debug("Verifying TOTP for user {}", username);
        boolean valid = googleAuthenticator.authorize(totpSecret, totpCode);
        if (!valid) {
            log.warn("Invalid TOTP code for user {}", username);
            throw new BadCredentialsException("Invalid TOTP code");
        }
        String authToken = jwtUtil.generateAuthToken(username);
        log.debug("Issued auth token for user {}", username);
        return authToken;
    }
}