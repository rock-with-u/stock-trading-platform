package com.isyoudwn.execution_service.trade_execution.application;

import java.time.LocalDateTime;

public record LimitOrderCreatedCommand(
        Long orderId,
        String accountNumber,
        String stockCode,
        String orderSide,
        long orderQuantity,
        long limitPrice,
        LocalDateTime orderedAt
) {
}
