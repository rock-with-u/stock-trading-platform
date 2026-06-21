package com.isyoudwn.account_service.order.infrastructure.outbox.message;

import java.time.LocalDateTime;

public record LimitOrderCreatedMessage(
        String eventId,
        Long orderId,
        String accountNumber,
        String stockCode,
        String orderSide,
        Long orderQuantity,
        Long limitPrice,
        LocalDateTime orderedAt,
        String idempotencyKey
) {
}
