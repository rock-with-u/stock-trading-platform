package com.isyoudwn.account_service.order.domain;

import com.isyoudwn.common_service.exception.StockOrderException;
import com.isyoudwn.common_service.response.ResponseMessage;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum StockOrderAction {
    NEW("신규 주문"),
    AMEND("주문 정정"),
    CANCEL("주문 취소");

    private final String description;

    public static StockOrderAction of(String action) {
        if (action == null || action.isBlank()) {
            throw new StockOrderException(ResponseMessage.INVALID_STOCK_ORDER_ACTION);
        }

        for (StockOrderAction stockOrderAction : values()) {
            if (action.equals(stockOrderAction.name())) {
                return stockOrderAction;
            }
        }

        throw new StockOrderException(ResponseMessage.INVALID_STOCK_ORDER_ACTION);
    }
}
