package com.biddingmate.biddinggo.auth.jwt;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class JwtCookieServiceImpl implements JwtCookieService {
    @Override
    public ResponseCookie createRefreshTokenCookie(String refreshToken, Duration duration) {

        return ResponseCookie
                .from("refresh_token", refreshToken)
                .httpOnly(true)
                .path("/")
                .maxAge(duration)
                .build();

    }

    @Override
    public ResponseCookie deleteRefreshTokenCookie() {
        return createRefreshTokenCookie("", Duration.ofSeconds(0));
    }

    @Override
    public HttpHeaders createRefreshTokenCookieHeaders(ResponseCookie cookie) {

        HttpHeaders headers = new HttpHeaders();

        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());

        return headers;

    }
}
