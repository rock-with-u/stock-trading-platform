package com.isyoudwn.market_service.quote.infrastructure.websocket;

import static com.isyoudwn.market_service.quote.infrastructure.websocket.KisQuoteFieldIndex.ASK_PRICE_START;
import static com.isyoudwn.market_service.quote.infrastructure.websocket.KisQuoteFieldIndex.ASK_QUANTITY_START;
import static com.isyoudwn.market_service.quote.infrastructure.websocket.KisQuoteFieldIndex.BID_PRICE_START;
import static com.isyoudwn.market_service.quote.infrastructure.websocket.KisQuoteFieldIndex.BID_QUANTITY_START;
import static com.isyoudwn.market_service.quote.infrastructure.websocket.KisQuoteFieldIndex.MIN_FIELD_COUNT;
import static com.isyoudwn.market_service.quote.infrastructure.websocket.KisQuoteFieldIndex.QUOTE_LEVEL_COUNT;
import static com.isyoudwn.market_service.quote.infrastructure.websocket.KisQuoteFieldIndex.STOCK_CODE;

import com.isyoudwn.market_service.quote.infrastructure.kafka.event.QuoteSnapshotEvent;
import com.isyoudwn.market_service.quote.infrastructure.kafka.event.QuoteSnapshotEvent.QuoteLevel;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class KisQuoteParser {

    public QuoteSnapshotEvent parse(
            String rawData,
            LocalDateTime receivedAt
    ) {
        String[] fields = rawData.split("\\^");

        if (fields.length < MIN_FIELD_COUNT) {
            throw new IllegalArgumentException(
                    "KIS 호가 필드 수가 부족합니다. fieldCount=" + fields.length
            );
        }

        String stockCode = fields[STOCK_CODE];

        List<QuoteLevel> asks = new ArrayList<>();
        List<QuoteLevel> bids = new ArrayList<>();

        for (int i = 0; i < QUOTE_LEVEL_COUNT; i++) {
            int level = i + 1;

            long askPrice = parseLong(fields[ASK_PRICE_START + i]);
            long bidPrice = parseLong(fields[BID_PRICE_START + i]);

            long askQuantity = parseLong(fields[ASK_QUANTITY_START + i]);
            long bidQuantity = parseLong(fields[BID_QUANTITY_START + i]);

            asks.add(new QuoteLevel(
                    level,
                    askPrice,
                    askQuantity
            ));

            bids.add(new QuoteLevel(
                    level,
                    bidPrice,
                    bidQuantity
            ));
        }

        return new QuoteSnapshotEvent(
                stockCode,
                receivedAt,
                asks,
                bids
        );
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
