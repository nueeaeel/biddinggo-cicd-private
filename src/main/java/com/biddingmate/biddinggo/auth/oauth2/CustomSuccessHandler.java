package com.biddingmate.biddinggo.auth.oauth2;

import com.biddingmate.biddinggo.auth.dto.CustomOAuth2Member;
import com.biddingmate.biddinggo.auth.jwt.JwtCookieService;
import com.biddingmate.biddinggo.auth.jwt.JwtProvider;
import com.biddingmate.biddinggo.common.exception.CustomException;
import com.biddingmate.biddinggo.common.exception.ErrorType;
import com.biddingmate.biddinggo.member.mapper.MemberMapper;
import com.biddingmate.biddinggo.member.model.Member;
import com.biddingmate.biddinggo.member.model.MemberStatus;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final JwtCookieService jwtCookieService;
    private final MemberMapper memberMapper;
    @Value("${FRONTEND_REDIRECT_URI:http://localhost:5173/auth/callback}")
    private String frontendRedirectUri;

    // oauth2 리팩터링
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        CustomOAuth2Member customUserDetails = (CustomOAuth2Member) authentication.getPrincipal();
        String username = customUserDetails.getMembername();

        log.info("[OAtuh2login] username: {}", username);
        Member member = memberMapper.selectMemberByUsername(username);

        // provider를 통해 토큰 발생

        if (member == null) {
            throw new CustomException(ErrorType.USER_NOT_FOUND);
        }

        if (member.getStatus() == MemberStatus.DELETED || member.getStatus() == MemberStatus.INACTIVE) {
            String redirectUrl = frontendRedirectUri
                    + "?error=" + URLEncoder.encode("login_blocked_member", StandardCharsets.UTF_8)
                    + "&message=" + URLEncoder.encode("탈퇴 또는 비활성화된 회원은 로그인할 수 없습니다.", StandardCharsets.UTF_8);
            ResponseCookie deleteCookie = jwtCookieService.deleteRefreshTokenCookie();
            response.addHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());
            getRedirectStrategy().sendRedirect(request, response, redirectUrl);
            return;
        }

        String refreshToken = jwtProvider.createRefreshToken(username);
        // JWTCookieService를 사용하여 Refresh 토큰 쿠키 생성 및 응답에 추가
        ResponseCookie cookie = jwtCookieService.createRefreshTokenCookie(refreshToken, Duration.ofDays(1));
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        getRedirectStrategy().sendRedirect(request, response, frontendRedirectUri);


    }
}
