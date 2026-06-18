package com.isyoudwn.common_service.response;

public record ApiResponse<T>(Boolean status, String code, String message, T data) {
    public static <T> ApiResponse<T> succeed(String code, String message, T data) {
        return new ApiResponse<>(true, code, message, data);
    }

    public static <T> ApiResponse<T> failure(String code, String message) {
        return new ApiResponse<>(true, code, message, null);
    }

    public static <T> ApiResponse<T> failure(String code, String message, T data) {
        return new ApiResponse<>(true, code, message, data);
    }
}
