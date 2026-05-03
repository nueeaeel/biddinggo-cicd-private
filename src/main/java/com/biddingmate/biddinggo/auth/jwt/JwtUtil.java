package com.biddingmate.biddinggo.auth.jwt;

import com.biddingmate.biddinggo.config.JWTProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

    private final String issuer;
    private final SecretKey secretKey;

    public JwtUtil(JWTProperties jwtProperties) {

        this.issuer = jwtProperties.getIssuer();
        this.secretKey = new SecretKeySpec(
                jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm()
        );
    }

    public String createJwtToken(Map<String, Object> claims, long expiration) {

        return Jwts.builder()

                .header().add("typ", "JWT").and()
                .claims(claims)
                .id(Long.toHexString(System.nanoTime()))
                .issuer(this.issuer)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(secretKey)
                .compact();
    }


    public String getJti(String token) {

        return getClaims(token).getId();
    }

    public String getUsername(String token) {

        return getClaims(token).get("username", String.class);

    }

    public String getTokenType(String token) {

        return getClaims(token).get("token_type", String.class);

    }

    public Long getIssuedAt(String token) {

        return getClaims(token).getIssuedAt().getTime();

    }

    public Long getExpiredAt(String token) {

        return getClaims(token).getExpiration().getTime();

    }

    // 토큰 유효 확인 메소드 (유효시 true, 만료시 false)
    public boolean validateToken(String token) {

        return !getClaims(token).getExpiration().before(new Date());

    }

    public Claims getClaims (String Token) {

        try {
            return Jwts
                    .parser()
                    .verifyWith(secretKey)
                    .build()
                    // 만료 되면 예외 발생되서 예외 발생
                    .parseSignedClaims(Token)
                    .getPayload();

        } catch (ExpiredJwtException e) {

            // 예외 발생되어도 토큰 정보는 넘겨야되니까 파싱한 정보 return
            return e.getClaims();
        }

    }



}
