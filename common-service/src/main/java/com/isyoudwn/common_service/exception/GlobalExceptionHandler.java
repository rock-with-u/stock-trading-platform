package com.isyoudwn.common_service.exception;

import com.isyoudwn.common_service.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountException.class)
    public ResponseEntity<ApiResponse<String>> handleAccountException(AccountException ex) {
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.failure(ex.getErrorCode(), ex.getMessage()));
    }

    @ExceptionHandler(MemberException.class)
    public ResponseEntity<ApiResponse<String>> handleMemberException(MemberException ex) {
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.failure(ex.getErrorCode(), ex.getMessage()));
    }

    @ExceptionHandler(TradeExecutionException.class)
    public ResponseEntity<ApiResponse<String>> handleTradeExecutionException(TradeExecutionException ex) {
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.failure(ex.getErrorCode(), ex.getMessage()));
    }
}
