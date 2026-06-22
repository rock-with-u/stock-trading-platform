package com.isyoudwn.market_service.trade.infrastructure.websocket;

import com.isyoudwn.market_service.trade.infrastructure.dto.KisTradeTickDto;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

@Component
public class KisTradeTickParser {

    private static final DateTimeFormatter TRADE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HHmmss");

    public KisTradeTickDto parse(String body) {
        String[] values = body.split("\\^");

        validate(values, body);

        String stockCode = values[KisTradeTickFieldIndex.STOCK_CODE];
        Long tradePrice = parseLong(values[KisTradeTickFieldIndex.TRADE_PRICE]);

        Long tradeVolume = getLongOrZero(values, KisTradeTickFieldIndex.TRADE_VOLUME);
        Long accumulatedVolume = getLongOrZero(values, KisTradeTickFieldIndex.ACCUMULATED_VOLUME);

        LocalDateTime tradedAt = LocalDateTime.of(
                LocalDate.now(),
                LocalTime.parse(
                        values[KisTradeTickFieldIndex.TRADE_TIME],
                        TRADE_TIME_FORMATTER
                )
        );

        return new KisTradeTickDto(
                stockCode,
                tradePrice,
                tradeVolume,
                accumulatedVolume,
                tradedAt
        );
    }

    private void validate(String[] values, String body) {
        if (values.length <= KisTradeTickFieldIndex.TRADE_PRICE) {
            throw new IllegalArgumentException("Invalid KIS trade tick body: " + body);
        }
    }

    private Long getLongOrZero(String[] values, int index) {
        if (values.length <= index) {
            return 0L;
        }

        return parseLong(values[index]);
    }

    private Long parseLong(String value) {
        if (value == null || value.isBlank()) {
            return 0L;
        }

        return Long.valueOf(value
                .replace(",", "")
                .replace("+", "")
                .replace("-", "")
                .trim()
        );
    }
}
