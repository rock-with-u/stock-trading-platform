package com.isyoudwn.execution_service.trade_execution.domain;

public enum RequestedOrderSide {
    BUY,
    SELL;

    public static RequestedOrderSide from(String value) {
        try {
            return RequestedOrderSide.valueOf(value);
        } catch (Exception e) {
            throw new IllegalArgumentException("지원하지 않는 주문 방향입니다. orderSide=" + value);
        }
    }
}
