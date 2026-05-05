package com.biddingmate.biddinggo.auth.controller;

import com.biddingmate.biddinggo.auth.dto.LoginResponse;
import com.biddingmate.biddinggo.auth.dto.RegisterUserInfoRequestDto;
import com.biddingmate.biddinggo.auth.jwt.JwtCookieService;
import com.biddingmate.biddinggo.auth.service.AuthService;
import com.biddingmate.biddinggo.common.response.ApiResponse;
import com.biddingmate.biddinggo.member.model.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth-social", description = "소셜 로그인")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtCookieService jwtCookieService;

    @GetMapping("/check")
    @Operation(summary = "인증 상태 확인")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkAuth(Authentication authentication) {

        // 1. 인증 정보가 아예 없는 경우
        if (authentication == null || !authentication.isAuthenticated()) {
            return ApiResponse.of(HttpStatus.UNAUTHORIZED, "auth-005", "인증 정보가 없습니다.", null);
        }

        // 2. 인증 객체에서 유저 정보 추출
        // authentication.getName()은 우리가 토큰을 만들 때 넣었던 username(google1133...)을 반환합니다.
        Map<String, Object> userInfo = new HashMap<>();
        if (authentication.getPrincipal() instanceof Member member) {
            userInfo.put("memberId", member.getId());
            userInfo.put("name", member.getName());
            userInfo.put("nickname", member.getNickname());
            userInfo.put("imageUrl", member.getImageUrl());
            userInfo.put("status", member.getStatus().name());
        }
        userInfo.put("username", authentication.getName());
        userInfo.put("role", authentication.getAuthorities());

        return ApiResponse.of(HttpStatus.OK, null, "인증 서버 검증 완료", userInfo);
    }

    @PatchMapping("/register")
    @Operation(summary = "필수 정보 입력")
    public ResponseEntity<ApiResponse<Void>> registerUserInfo(
            Authentication authentication,
            @Valid @RequestBody RegisterUserInfoRequestDto requestData) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ApiResponse.of(HttpStatus.UNAUTHORIZED, "auth-005", "인증 정보가 없습니다.", null);
        }

        // 토큰에서 추출된 username (google1133...)
        String username = authentication.getName();

        authService.updateInfo(
                username,
                requestData.getName(),
                requestData.getNickname(),
                requestData.getImageUrl()
        );

        return ApiResponse.of(HttpStatus.OK, null, "필수 정보 입력 완료", null);

        }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃")
    public ResponseEntity<Void> logout(
            @Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false) String bearerToken
    ) {

        authService.logout(bearerToken);
        ResponseCookie responseCookie = jwtCookieService.deleteRefreshTokenCookie();
        HttpHeaders headers = jwtCookieService.createRefreshTokenCookieHeaders(responseCookie);

        return ResponseEntity
                .noContent()
                .headers(headers)
                .build();

    }

    @PostMapping("/refresh")
    @Operation(summary = "토근 재발급")
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(
            @Parameter(hidden = true) @CookieValue(name = "refresh_token", defaultValue = "") String refreshToken,
            HttpServletResponse response) {

        LoginResponse loginResponse;
        try {
            loginResponse = authService.refreshAccessToken(refreshToken);
        } catch (RuntimeException e) {
            ResponseCookie responseCookie = jwtCookieService.deleteRefreshTokenCookie();
            response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
            throw e;
        }

        return ApiResponse.of(HttpStatus.OK,null, "토근 재발급 완료", loginResponse);

    }


}
