package com.biddingmate.biddinggo.auth.oauth2;

import com.biddingmate.biddinggo.auth.jwt.JwtCookieService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final JwtCookieService jwtCookieService;

    @Value("${FRONTEND_REDIRECT_URI:http://localhost:5173/auth/callback}")
    private String frontendRedirectUri;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception)
            throws IOException, ServletException {

        // Convert OAuth2 authentication failures into the frontend callback format
        // so the client can show a controlled error message instead of falling back to /login.
        String errorCode = "oauth2_login_failed";
        String message = "소셜 로그인에 실패했습니다.";

        if (exception instanceof OAuth2AuthenticationException oauth2Exception) {
            errorCode = oauth2Exception.getError().getErrorCode();
            if (oauth2Exception.getMessage() != null && !oauth2Exception.getMessage().isBlank()) {
                message = oauth2Exception.getMessage();
            }
        }

        String redirectUrl = frontendRedirectUri
                + "?error=" + URLEncoder.encode(errorCode, StandardCharsets.UTF_8)
                + "&message=" + URLEncoder.encode(message, StandardCharsets.UTF_8);

        ResponseCookie deleteCookie = jwtCookieService.deleteRefreshTokenCookie();
        response.addHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());

        log.warn("[OAuth2 login failed] errorCode={}, message={}", errorCode, message);
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
