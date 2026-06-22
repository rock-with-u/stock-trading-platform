package com.isyoudwn.market_service.trade.infrastructure.kafka.event;

import java.time.LocalDateTime;

public record TickSnapshotEvent(
        String stockCode,
        long tradePrice,
        long tradeVolume,
        long accumulatedVolume,
        LocalDateTime receivedAt

) {
}
