package com.biddingmate.biddinggo.auth.jwt;

import com.biddingmate.biddinggo.auth.dto.LoginResponse;
import com.biddingmate.biddinggo.common.exception.CustomException;
import com.biddingmate.biddinggo.common.exception.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final RedisTemplate<String, String> redisTemplate;
    private static final long ACCESS_TOKEN_EXPIRATION = 1000L * 60 * 30; // 30분
    private static final long REFRESH_TOKEN_EXPIRATION = 1000L * 60 * 60L * 24L; // 1일

    public String createAccessToken(String username, List<String> authorities, String status) {


        Map<String, Object> claims =
                Map.of("username", username,
                        "authorities", authorities,
                        "status", status,
                        "token_type", "access");


        return jwtUtil.createJwtToken(claims, ACCESS_TOKEN_EXPIRATION);


    }

    // 클라이언트가 헤더를 통해 서버로 전달한 토큰을 추출
    public String resolveToken(String bearerToken) {

        if (bearerToken != null && bearerToken.startsWith("Bearer")) {

            return bearerToken.substring(7);
        }
        return null;
    }

    // 엑세스 토큰 무결성과 유효성 검증 & 블랙리스트 확인
    public boolean isUsableAccessToken(String accessToken) {

        return accessToken != null
                && jwtUtil.validateToken(accessToken)
                && !isBlacklisted(accessToken)
                && isAccessToken(accessToken);


    }

    // securityContext 객체에 저장될 Authentication 객체를 생성
    public Authentication createAuthentication(String token) {

        String username = jwtUtil.getUsername(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }

    public void addBlacklist(String accessToken) {

        String blacklistKey = String.format("blacklist:%S", jwtUtil.getJti(accessToken));

        redisWrite(() -> redisTemplate.opsForValue()
                .set(blacklistKey, accessToken, ACCESS_TOKEN_EXPIRATION, TimeUnit.MILLISECONDS));

    }

    private boolean isBlacklisted(String accessToken) {

        String blacklistKey = String.format("blacklist:%S", jwtUtil.getJti(accessToken));

        return redisRead(() -> redisTemplate.hasKey(blacklistKey));
    }

    // 리프레시 토큰 제거
    public void deleteRefreshToken(String accessToken) {
        String username = jwtUtil.getUsername(accessToken);

        deleteRefreshTokenByUsername(username);

    }

    public void deleteRefreshTokenByUsername(String username) {
        redisWrite(() -> redisTemplate.delete(String.format("refresh:%S", username)));
    }

    // 리프레시 토큰 생성
    public String createRefreshToken(String username) {

        Map<String, Object> claims =
                Map.of("username", username, "token_type", "refresh");

        String refreshToken = jwtUtil.createJwtToken(claims, REFRESH_TOKEN_EXPIRATION);
        String refreshKey = String.format("refresh:%S", username);

        redisWrite(() -> redisTemplate.opsForValue()
                .set(refreshKey, refreshToken, REFRESH_TOKEN_EXPIRATION, TimeUnit.MILLISECONDS));

        return refreshToken;

    }

    public boolean isValidRefresh(String refreshToken) {

        String username = jwtUtil.getUsername(refreshToken);
        String storedRefreshToken = redisRead(() ->
                redisTemplate.opsForValue().get(String.format("refresh:%S", username)));

        return storedRefreshToken != null && storedRefreshToken.equals(refreshToken);

    }

    // Oauth2 리펙터링
    public Map<String, Object> createTotalTokenResponse(String username, List<String> authorities, String status) {

        // access token 생성
        String accessToken = createAccessToken(username, authorities, status);

        LoginResponse loginResponse = LoginResponse.builder()
                .accessToken(accessToken)
                .type("Bearer")
                .username(username)
                .status(status)
                .authorities(authorities)
                .issuedAt(jwtUtil.getIssuedAt(accessToken))
                .expiredAt(jwtUtil.getExpiredAt(accessToken))
                .build();


        String refreshToken = createRefreshToken(username);


        return Map.of(
                "loginResponse", loginResponse, // 프론트 전달용 (URL 파라미터용)
                "refreshToken", refreshToken    // 쿠키 생성용 (JWTCookieService용)
        );


    }

    private boolean isAccessToken(String accessToken) {

        return jwtUtil.getTokenType(accessToken).equals("access");
    }

    private void redisWrite(Runnable action) {

        try {
            action.run();
        } catch (DataAccessException e) {

            throw new CustomException(ErrorType.REDIS_UNAVAILABLE);
        }

    }

    private <T> T redisRead(java.util.function.Supplier<T> action) {

        try {
            return action.get();

        } catch (DataAccessException e) {

            throw new CustomException(ErrorType.REDIS_UNAVAILABLE);
        }

    }
}
