package com.isyoudwn.account_service.order.domain;

import com.isyoudwn.common_service.exception.StockOrderException;
import com.isyoudwn.common_service.response.ResponseMessage;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum OrderPriceType {
    MARKET("시장가"),
    LIMIT("지정가");

    private final String description;

    public static OrderPriceType of(String type) {
        if (type == null || type.isBlank()) {
            throw new StockOrderException(ResponseMessage.INVALID_STOCK_ORDER_TYPE);
        }

        for (OrderPriceType stockOrderType : values()) {
            if (type.equals(stockOrderType.name())) {
                return stockOrderType;
            }
        }

        throw new StockOrderException(ResponseMessage.INVALID_STOCK_ORDER_TYPE);
    }
}
