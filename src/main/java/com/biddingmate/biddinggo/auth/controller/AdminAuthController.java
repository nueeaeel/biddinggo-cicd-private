package com.biddingmate.biddinggo.auth.controller;

import com.biddingmate.biddinggo.auth.dto.AdminLoginRequestDto;
import com.biddingmate.biddinggo.auth.dto.AdminSignupRequestDto;
import com.biddingmate.biddinggo.auth.dto.LoginResponse;
import com.biddingmate.biddinggo.auth.jwt.JwtCookieService;
import com.biddingmate.biddinggo.auth.service.AuthService;
import com.biddingmate.biddinggo.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/api/v1/admin/auth")
@RequiredArgsConstructor
@Tag(name = "Auth-admin", description = "어드민 로그인")
public class AdminAuthController {

    private final AuthService authService;
    private final JwtCookieService jwtCookieService;

    @Operation(summary = "로그인")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody AdminLoginRequestDto loginRequestDto) {

        LoginResponse loginResponse = authService.login(
                loginRequestDto.getUsername(),
                loginRequestDto.getPassword()

        );

        String refreshToken = authService.createRefreshToken(loginResponse.getUsername());
        ResponseCookie cookie =
                jwtCookieService.createRefreshTokenCookie(refreshToken, Duration.ofDays(1));
        HttpHeaders headers = jwtCookieService.createRefreshTokenCookieHeaders(cookie);

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(ApiResponse.<LoginResponse>builder()
                        .status(HttpStatus.OK.value())
                        .errorCode(null)
                        .message("로그인 성공")
                        .result(loginResponse)
                        .build());

    }

    @Operation(summary = "회원가입")
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<String>> signup(
            @Valid @RequestBody AdminSignupRequestDto signupRequestDto
    ) {

        authService.signup(signupRequestDto);

        return ApiResponse.of(HttpStatus.CREATED, null, "회원가입완료",null);

    }


}
