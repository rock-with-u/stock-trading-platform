package com.isyoudwn.account_service.domain.order;

import com.isyoudwn.common_service.exception.StockOrderException;
import com.isyoudwn.common_service.response.ResponseMessage;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum OrderSide {
    BUY("매수"),
    SELL("매도");

    private final String description;

    public static OrderSide of(String requestedOrderSide) {
        if (requestedOrderSide == null || requestedOrderSide.isBlank()) {
            throw new StockOrderException(ResponseMessage.INVALID_STOCK_ORDER_SIDE);
        }

        for (OrderSide orderSide : values()) {
            if (requestedOrderSide.equals(orderSide.name())) {
                return orderSide;
            }
        }

        throw new StockOrderException(ResponseMessage.INVALID_STOCK_ORDER_SIDE);
    }
}
