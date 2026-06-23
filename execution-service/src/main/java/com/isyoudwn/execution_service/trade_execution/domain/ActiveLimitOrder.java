package com.isyoudwn.execution_service.trade_execution.domain;

import java.time.LocalDateTime;

public record ActiveLimitOrder(
        Long orderId,
        String accountNumber,
        String stockCode,
        String orderSide,
        long remainingQuantity,
        long limitPrice,
        LocalDateTime orderedAt
) {

    public boolean isBuy() {
        return "BUY".equals(orderSide);
    }

    public boolean isSell() {
        return "SELL".equals(orderSide);
    }
}
