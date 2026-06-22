package com.isyoudwn.market_service.trade.infrastructure.websocket;

import com.isyoudwn.market_service.common.websocket.KisRealtimeTransactionId;
import com.isyoudwn.market_service.trade.infrastructure.dto.KisTradeTickDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KisTradeWebSocketMessageHandler {

    private final KisTradeTickParser kisTradeTickParser;

    public void handleMessage(String message) {
        if (message == null || message.isBlank()) {
            return;
        }

        if (message.startsWith("{")) {
            handleJsonMessage(message);
            return;
        }

        String[] parts = message.split("\\|", 4);

        if (parts.length < 4) {
            log.warn("Invalid KIS trade websocket message={}", message);
            return;
        }

        String encrypted = parts[0];
        String transactionId = parts[1];
        String dataCount = parts[2];
        String body = parts[3];

        if (!"0".equals(encrypted)) {
            log.warn("Encrypted KIS trade websocket message received. transactionId={}", transactionId);
            return;
        }

        if (KisRealtimeTransactionId.STOCK_TRADE.getCode().equals(transactionId)) {
            handleTradeTick(dataCount, body);
            return;
        }

        log.debug("Unsupported KIS realtime transactionId={}, body={}", transactionId, body);
    }

    private void handleTradeTick(
            String dataCount,
            String body
    ) {
        KisTradeTickDto tradeTick = kisTradeTickParser.parse(body);

        log.info(
                "KIS 실시간 체결가 수신 dataCount={}, stockCode={}, tradePrice={}, tradeVolume={}, accumulatedVolume={}, tradedAt={}",
                dataCount,
                tradeTick.stockCode(),
                tradeTick.tradePrice(),
                tradeTick.tradeVolume(),
                tradeTick.accumulatedVolume(),
                tradeTick.tradedAt()
        );
    }

    private void handleJsonMessage(String message) {
        log.info("KIS trade websocket response={}", message);
    }
}
