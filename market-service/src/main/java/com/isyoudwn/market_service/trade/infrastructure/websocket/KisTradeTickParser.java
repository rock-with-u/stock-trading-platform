package com.isyoudwn.market_service.trade.infrastructure.websocket;

import com.isyoudwn.market_service.trade.infrastructure.kafka.event.TickSnapshotEvent;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

@Component
public class KisTradeTickParser {

    private static final DateTimeFormatter TRADE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HHmmss");

    public TickSnapshotEvent parse(
            String body,
            LocalDateTime receivedAt
    ) {
        String[] values = body.split("\\^", -1);

        validate(values, body);

        String stockCode = values[KisTradeTickFieldIndex.STOCK_CODE];
        long tradePrice = parseLong(values[KisTradeTickFieldIndex.TRADE_PRICE]);

        long tradeVolume = getLongOrZero(values, KisTradeTickFieldIndex.TRADE_VOLUME);
        long accumulatedVolume = getLongOrZero(values, KisTradeTickFieldIndex.ACCUMULATED_VOLUME);

        LocalDateTime tradedAt = parseTradedAt(
                values[KisTradeTickFieldIndex.TRADE_TIME],
                receivedAt
        );

        return new TickSnapshotEvent(
                stockCode,
                tradePrice,
                tradeVolume,
                accumulatedVolume,
                tradedAt
        );
    }

    private LocalDateTime parseTradedAt(
            String tradeTime,
            LocalDateTime receivedAt
    ) {
        LocalTime parsedTradeTime = LocalTime.parse(
                tradeTime,
                TRADE_TIME_FORMATTER
        );

        return LocalDateTime.of(
                receivedAt.toLocalDate(),
                parsedTradeTime
        );
    }

    private void validate(String[] values, String body) {
        if (values.length <= KisTradeTickFieldIndex.TRADE_PRICE) {
            throw new IllegalArgumentException("Invalid KIS trade tick body: " + body);
        }
    }

    private long getLongOrZero(String[] values, int index) {
        if (values.length <= index) {
            return 0L;
        }

        return parseLong(values[index]);
    }

    private long parseLong(String value) {
        if (value == null || value.isBlank()) {
            return 0L;
        }

        return Long.parseLong(
                value
                        .replace(",", "")
                        .replace("+", "")
                        .replace("-", "")
                        .trim()
        );
    }
}
