package com.biddingmate.biddinggo.auth.handler;

import com.biddingmate.biddinggo.common.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

/**
 *  인증실패 처리 핸들러
 *  인증되지 않은 사용자가 보호된 리소스에 접근하려고 할 때 호출
 *  401 UNAUTHORIZED 상태 코드와 함께 apiResponse을 JSON 형태로 반환
 */
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        // HTTP 응답 헤더 설정
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // 공통 응답 객체 생성
        ObjectMapper objectMapper = new ObjectMapper();
        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .errorCode(HttpStatus.UNAUTHORIZED.name())
                .message("인증이 필요한 서비스입니다. 로그인해주세요")
                .result(null)
                .build();

        // JSON으로 변환 후 전송
        response.getWriter().write(
                objectMapper.writeValueAsString(apiResponse)
        );

    }
}
