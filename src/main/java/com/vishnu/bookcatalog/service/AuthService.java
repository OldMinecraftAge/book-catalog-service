package com.vishnu.bookcatalog.service;

import com.vishnu.bookcatalog.dto.LoginRequest;

public interface AuthService {

    /**
     * Validates username and password, and issues a one-time OTP token if successful.
     *
     * @param request containing username and password
     * @return a one-time OTP token for second-factor verification
     */
    String authenticateCredentials(LoginRequest request);

    /**
     * Validates the provided TOTP code against the one-time token and issues the final authentication JWT.
     *
     * @param otpToken the one-time OTP token
     * @param totpCode the 6-digit TOTP code
     * @return the final JWT auth token
     */
    String verifyTotpAndIssueToken(String otpToken, int totpCode);
}
