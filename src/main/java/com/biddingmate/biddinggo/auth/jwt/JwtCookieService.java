package com.biddingmate.biddinggo.auth.jwt;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import java.time.Duration;

public interface JwtCookieService {

    ResponseCookie createRefreshTokenCookie(String refreshToken, Duration duration);

    ResponseCookie deleteRefreshTokenCookie();

    HttpHeaders createRefreshTokenCookieHeaders(ResponseCookie cookie);
}
