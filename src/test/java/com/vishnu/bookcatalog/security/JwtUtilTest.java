package com.vishnu.bookcatalog.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private static final String VALID_SECRET = "ABCDEFGHIJKLMNOPQRSTUVWXZY0123456789ABCDEF";

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "jwtSecret", VALID_SECRET);
        ReflectionTestUtils.setField(jwtUtil, "otpExpirationMinutes", 5L);
        ReflectionTestUtils.setField(jwtUtil, "authExpirationMinutes", 60L);
        jwtUtil.init();
    }

    @Test
    void init_withShortSecret_shouldThrow() {
        JwtUtil util = new JwtUtil();
        ReflectionTestUtils.setField(util, "jwtSecret", "short_secret");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, util::init);
        assertTrue(ex.getMessage().contains("at least 32 characters"));
    }

    @Test
    void generateAndValidateOtpToken_success() {
        String username = "testUser";
        String token = jwtUtil.generateOtpToken(username);
        assertNotNull(token, "OTP token should not be null");

        String extracted = jwtUtil.validateOtpTokenAndGetUsername(token);
        assertEquals(username, extracted, "Extracted username should match");
    }

    @Test
    void validateOtpToken_tokenWithoutPending_shouldThrow() {
        String authToken = jwtUtil.generateAuthToken("testUser");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> jwtUtil.validateOtpTokenAndGetUsername(authToken)
        );
        assertTrue(ex.getMessage().contains("Invalid or expired OTP token"));
    }

    @Test
    void validateOtpToken_invalidToken_shouldThrow() {
        assertThrows(IllegalArgumentException.class,
                () -> jwtUtil.validateOtpTokenAndGetUsername("invalid.token.string")
        );
    }

    @Test
    void expiredOtpToken_shouldThrow() {
        ReflectionTestUtils.setField(jwtUtil, "otpExpirationMinutes", -1L);
        String token = jwtUtil.generateOtpToken("testUser");
        assertThrows(IllegalArgumentException.class,
                () -> jwtUtil.validateOtpTokenAndGetUsername(token)
        );
    }

    @Test
    void generateAndValidateAuthToken_success() {
        String username = "testUser";
        String token = jwtUtil.generateAuthToken(username);
        assertNotNull(token, "Auth token should not be null");

        String extracted = jwtUtil.validateAuthTokenAndGetUsername(token);
        assertEquals(username, extracted, "Extracted username should match");
    }

    @Test
    void validateAuthToken_invalidToken_shouldThrow() {
        assertThrows(IllegalArgumentException.class,
                () -> jwtUtil.validateAuthTokenAndGetUsername("bad.token.here")
        );
    }

    @Test
    void expiredAuthToken_shouldThrow() {
        ReflectionTestUtils.setField(jwtUtil, "authExpirationMinutes", -1L);
        String token = jwtUtil.generateAuthToken("testUser");
        assertThrows(IllegalArgumentException.class,
                () -> jwtUtil.validateAuthTokenAndGetUsername(token)
        );
    }
}
