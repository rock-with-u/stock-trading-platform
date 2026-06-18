package com.isyoudwn.account_service.domain;

import com.isyoudwn.common_service.exception.AccountException;
import com.isyoudwn.common_service.response.ResponseMessage;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PostingSourceType {
    CASH_TRANSACTION("현금 거래"),
    STOCK_ORDER("주식 주문"),
    TRADE_EXECUTION("매매 체결"),
    TRADE_SETTLEMENT("매매 결제");

    private final String description;

    public static PostingSourceType of(String type) {
        if (type == null || type.isBlank()) {
            throw new AccountException(ResponseMessage.INVALID_POSTING_SOURCE_TYPE);
        }

        for (PostingSourceType postingSourceType : values()) {
            if (type.equals(postingSourceType.name())) {
                return postingSourceType;
            }
        }

        throw new AccountException(ResponseMessage.INVALID_POSTING_SOURCE_TYPE);
    }
}
