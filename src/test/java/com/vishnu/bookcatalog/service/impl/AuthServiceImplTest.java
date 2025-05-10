package com.vishnu.bookcatalog.service.impl;

import com.vishnu.bookcatalog.dto.LoginRequest;
import com.vishnu.bookcatalog.security.JwtUtil;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authManager;

    @Mock
    private GoogleAuthenticator googleAuthenticator;

    @Mock
    private JwtUtil jwtUtil;

    private AuthServiceImpl authService;

    private LoginRequest loginRequest;
    private final String rawOtpToken = "rawOtpToken";
    private final int totpCode = 123456;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authService = new AuthServiceImpl(
                authManager,
                googleAuthenticator,
                jwtUtil,
                "TESTSECRET"
        );
        loginRequest = new LoginRequest("admin", "password");
    }

    @Test
    void authenticateCredentials_Success() {
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(Authentication.class));
        when(jwtUtil.generateOtpToken(eq("admin"))).thenReturn(rawOtpToken);

        String token = authService.authenticateCredentials(loginRequest);

        assertEquals(rawOtpToken, token);
        verify(authManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil).generateOtpToken("admin");
    }

    @Test
    void authenticateCredentials_InvalidCredentials_Throws() {
        when(authManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad creds"));

        assertThrows(BadCredentialsException.class, () -> authService.authenticateCredentials(loginRequest));
    }

    @Test
    void verifyTotpAndIssueToken_Success() {
        final String finalAuthToken = "finalAuthToken";
        when(jwtUtil.validateOtpTokenAndGetUsername(eq(rawOtpToken))).thenReturn("admin");
        when(googleAuthenticator.authorize(anyString(), eq(totpCode))).thenReturn(true);
        when(jwtUtil.generateAuthToken(eq("admin"))).thenReturn(finalAuthToken);

        String token = authService.verifyTotpAndIssueToken(rawOtpToken, totpCode);

        assertEquals(finalAuthToken, token);
        verify(jwtUtil).validateOtpTokenAndGetUsername(rawOtpToken);
        verify(googleAuthenticator).authorize(anyString(), eq(totpCode));
        verify(jwtUtil).generateAuthToken("admin");
    }

    @Test
    void verifyTotpAndIssueToken_InvalidOtpToken_Throws() {
        when(jwtUtil.validateOtpTokenAndGetUsername(eq(rawOtpToken)))
                .thenThrow(new IllegalArgumentException("Invalid OTP token"));

        assertThrows(IllegalArgumentException.class, () -> authService.verifyTotpAndIssueToken(rawOtpToken, totpCode));
    }

    @Test
    void verifyTotpAndIssueToken_InvalidTotpCode_Throws() {
        when(jwtUtil.validateOtpTokenAndGetUsername(eq(rawOtpToken))).thenReturn("admin");
        when(googleAuthenticator.authorize(anyString(), eq(totpCode))).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> authService.verifyTotpAndIssueToken(rawOtpToken, totpCode));
    }
}