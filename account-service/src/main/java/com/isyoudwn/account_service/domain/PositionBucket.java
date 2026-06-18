package com.isyoudwn.account_service.domain;

import com.isyoudwn.common_service.exception.AccountException;
import com.isyoudwn.common_service.response.ResponseMessage;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PositionBucket {
    HOLDING_QUANTITY("보유 수량"),
    SELL_RESERVED_QUANTITY("매도 예약 수량");

    private final String description;

    public static PositionBucket of(String bucket) {
        if (bucket == null || bucket.isBlank()) {
            throw new AccountException(ResponseMessage.INVALID_POSITION_BUCKET);
        }

        for (PositionBucket positionBucket : values()) {
            if (bucket.equals(positionBucket.name())) {
                return positionBucket;
            }
        }

        throw new AccountException(ResponseMessage.INVALID_POSITION_BUCKET);
    }
}
