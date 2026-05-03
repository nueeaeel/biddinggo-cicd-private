package com.biddingmate.biddinggo.auth.handler;

import com.biddingmate.biddinggo.common.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

/**
 *  인가 거부 처리 핸들러
 *  인증은 완료했으나, 해당 리소스에 접근할 권한이 없는 경우 호출된다.
 *  (예: 일반 사용자가 관리자 전용 API를 호출할 때)
 *  403 FORBIDDEN 상태 코드와 함께 apiResponse을 JSON 형태로 반환
 */
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {

        // HTTP 응답 헤더 설정
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // 공통 응답 객체 생성
        ObjectMapper objectMapper = new ObjectMapper();
        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .status(HttpStatus.FORBIDDEN.value())
                .errorCode(HttpStatus.FORBIDDEN.name())
                .message("접근 권한이 없습니다.")
                .result(null)
                .build();

        // JSON으로 변환 후 전송
        response.getWriter().write(
                objectMapper.writeValueAsString(apiResponse)
        );


    }
}
