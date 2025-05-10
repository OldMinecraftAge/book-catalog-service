package com.vishnu.bookcatalog.controller;

import com.vishnu.bookcatalog.dto.AuthTokenResponse;
import com.vishnu.bookcatalog.dto.LoginRequest;
import com.vishnu.bookcatalog.dto.OtpTokenResponse;
import com.vishnu.bookcatalog.dto.TotpRequest;
import com.vishnu.bookcatalog.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Two-step login with credentials + TOTP")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Step 1: Login with username & password",
            description = "Validates credentials and returns a one-time OTP token if successful")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Credentials valid – OTP token issued"),
            @ApiResponse(responseCode = "401", description = "Invalid username or password")
    })
    @PostMapping("/login")
    public ResponseEntity<OtpTokenResponse> login(
            @Valid @RequestBody LoginRequest request) {

        String otpToken = authService.authenticateCredentials(request);
        return ResponseEntity.ok(new OtpTokenResponse(otpToken));
    }

    @Operation(summary = "Step 2: Verify TOTP code",
            description = "Submits a 6-digit code against a prior OTP token to receive the final auth token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "TOTP valid – Auth token issued"),
            @ApiResponse(responseCode = "401", description = "Invalid or expired OTP token or TOTP code")
    })
    @PostMapping("/2fa")
    public ResponseEntity<AuthTokenResponse> verifyTotp(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String bearerOtpToken,
            @Valid @RequestBody TotpRequest request) {

        String token = bearerOtpToken.replaceFirst("^Bearer ", "");
        String authToken = authService.verifyTotpAndIssueToken(token, request.totpCode());
        return ResponseEntity.ok(new AuthTokenResponse(authToken));
    }
}
