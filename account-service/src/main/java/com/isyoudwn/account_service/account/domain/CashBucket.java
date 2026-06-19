package com.isyoudwn.account_service.account.domain;

import com.isyoudwn.common_service.exception.AccountException;
import com.isyoudwn.common_service.response.ResponseMessage;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum CashBucket {
    SETTLED_CASH("사용 가능한 결제 완료 현금"),
    RESERVED_BUY("매수 주문 예약금"),
    UNSETTLED_SELL_RECEIVABLE("미결제 매도 대금");

    private final String description;

    public static CashBucket of(String bucket) {
        if (bucket == null || bucket.isBlank()) {
            throw new AccountException(ResponseMessage.INVALID_CASH_BUCKET);
        }

        for (CashBucket cashBucket : values()) {
            if (bucket.equals(cashBucket.name())) {
                return cashBucket;
            }
        }

        throw new AccountException(ResponseMessage.INVALID_CASH_BUCKET);
    }
}
