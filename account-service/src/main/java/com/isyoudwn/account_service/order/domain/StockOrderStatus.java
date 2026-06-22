package com.isyoudwn.account_service.order.domain;

import com.isyoudwn.common_service.exception.StockOrderException;
import com.isyoudwn.common_service.response.ResponseMessage;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum StockOrderStatus {
    PENDING("대기"),
    PARTIALLY_FILLED("부분 체결"),
    FILLED("전량 체결"),
    CANCELED("주문 취소"),
    REJECTED("주문 거절"),
    EXPIRED("만료");

    private final String description;

    public static StockOrderStatus of(String status) {
        if (status == null || status.isBlank()) {
            throw new StockOrderException(ResponseMessage.INVALID_STOCK_ORDER_STATUS);
        }

        for (StockOrderStatus stockOrderStatus : values()) {
            if (status.equals(stockOrderStatus.name())) {
                return stockOrderStatus;
            }
        }

        throw new StockOrderException(ResponseMessage.INVALID_STOCK_ORDER_STATUS);
    }
}
