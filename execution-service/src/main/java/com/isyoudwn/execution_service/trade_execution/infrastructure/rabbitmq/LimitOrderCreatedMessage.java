package com.isyoudwn.execution_service.trade_execution.infrastructure.rabbitmq;

import java.time.LocalDateTime;

public record LimitOrderCreatedMessage(
        Long orderId,
        String accountNumber,
        String stockCode,
        String orderSide,
        long orderQuantity,
        long limitPrice,
        LocalDateTime orderedAt
) {
}
