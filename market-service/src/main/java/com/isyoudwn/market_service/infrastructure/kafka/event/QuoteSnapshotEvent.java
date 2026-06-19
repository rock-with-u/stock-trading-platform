package com.isyoudwn.market_service.infrastructure.kafka.event;

import java.time.LocalDateTime;
import java.util.List;

public record QuoteSnapshotEvent(
        String stockCode,
        LocalDateTime receivedAt,
        List<QuoteLevel> asks, // 매도
        List<QuoteLevel> bids // 매수
) {

    public record QuoteLevel(
            int level, // 1(즉시 체결에 가까운 가격) -> 10(비교적 먼 가격)
            long price,
            long quantity
    ) {
    }
}
