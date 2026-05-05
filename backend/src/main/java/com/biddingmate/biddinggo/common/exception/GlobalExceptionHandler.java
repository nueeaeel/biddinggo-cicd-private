package com.biddingmate.biddinggo.common.exception;

import com.biddingmate.biddinggo.common.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    // 커스텀 비즈니스 예외 처리
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustomException(CustomException e) {
        ErrorType errorType = e.getErrorType();
        log.error("비즈니스 로직 예외 발생 - Code: {}, Message: {}", errorType.getErrorCode(), errorType.getMessage());

        return ApiResponse.of(
                errorType.getHttpStatus(),
                errorType.getErrorCode(),
                e.getMessage(),
                null
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldErrors().stream().findFirst().orElse(null);
        String message = fieldError != null ? fieldError.getDefaultMessage() : ErrorType.BAD_REQUEST.getMessage();

        log.warn("요청 검증 예외 발생 - Message: {}", message);

        return ApiResponse.of(
                ErrorType.BAD_REQUEST.getHttpStatus(),
                ErrorType.BAD_REQUEST.getErrorCode(),
                message,
                null
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("권한 거부 예외 발생 - Message: {}", e.getMessage());

        return ApiResponse.of(
                ErrorType.FORBIDDEN.getHttpStatus(),
                ErrorType.FORBIDDEN.getErrorCode(),
                ErrorType.FORBIDDEN.getMessage(),
                null
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("서버 내부 예외 발생 - Message: {}", e.getMessage());

        return ApiResponse.of(
                ErrorType.INTERNAL_ERROR.getHttpStatus(),
                ErrorType.INTERNAL_ERROR.getErrorCode(),
                e.getMessage(),
                null
        );
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(AuthenticationException e) {
        log.warn("인증 실패 예외 발생 - Message: {}", e.getMessage());

        return ApiResponse.of(
                ErrorType.UNAUTHORIZED.getHttpStatus(),
                ErrorType.UNAUTHORIZED.getErrorCode(),
                ErrorType.UNAUTHORIZED.getMessage(),
                null
        );
    }
}
