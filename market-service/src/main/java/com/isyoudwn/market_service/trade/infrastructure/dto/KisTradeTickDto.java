package com.isyoudwn.market_service.trade.infrastructure.dto;

import java.time.LocalDateTime;

public record KisTradeTickDto(
        String stockCode,
        Long tradePrice,
        Long tradeVolume,
        Long accumulatedVolume,
        LocalDateTime tradedAt
) {
}
