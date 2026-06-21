package com.isyoudwn.account_service.account.infrastructure;

import java.time.LocalDateTime;

public record OrderCreatedMessage(
        Long orderId,
        String accountNumber,
        String stockCode,
        String orderSide,
        String priceType,
        Long orderQuantity,
        Long limitPrice,
        LocalDateTime orderedAt,
        String idempotencyKey
) {
}
