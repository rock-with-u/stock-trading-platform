package com.isyoudwn.common_service.exception;

import com.isyoudwn.common_service.response.ResponseMessage;

public class KisException extends RuntimeException {

    private final ResponseMessage responseMessage;

    public KisException(ResponseMessage responseMessage) {
        super(responseMessage.getMessage());
        this.responseMessage = responseMessage;
    }

    public String getErrorCode() {
        return responseMessage.getCode();
    }
}
