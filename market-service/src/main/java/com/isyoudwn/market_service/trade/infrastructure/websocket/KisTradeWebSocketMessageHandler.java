package com.isyoudwn.market_service.trade.infrastructure.websocket;

import com.isyoudwn.common_service.config.TimeProvider;
import com.isyoudwn.market_service.common.websocket.KisRealtimeMessageParser;
import com.isyoudwn.market_service.common.websocket.KisRealtimeTransactionId;
import com.isyoudwn.market_service.common.websocket.ParsedRealtimeMessageDto;
import com.isyoudwn.market_service.trade.infrastructure.kafka.event.TickSnapshotEvent;
import com.isyoudwn.market_service.trade.infrastructure.kafka.producer.TickEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KisTradeWebSocketMessageHandler {

    private final KisTradeTickParser kisTradeTickParser;
    private final TickEventProducer tickEventProducer;
    private final KisRealtimeMessageParser realtimeMessageParser;
    private final TimeProvider timeProvider;

    public void handleMessage(String message) {
        if (message == null || message.isBlank()) {
            return;
        }

        if (message.startsWith("{")) {
            handleJsonMessage(message);
            return;
        }

        ParsedRealtimeMessageDto realtimeMessage = realtimeMessageParser.parse(message);

        if (realtimeMessage == null) {
            return;
        }
        handleRealtimeMessage(realtimeMessage);
    }

    private void handleRealtimeMessage(ParsedRealtimeMessageDto realtimeMessage) {
        if (!realtimeMessage.isPlainText()) {
            log.warn(
                    "Encrypted KIS trade websocket message received. transactionId={}",
                    realtimeMessage.transactionId()
            );
            return;
        }

        if (KisRealtimeTransactionId.STOCK_TRADE.getCode().equals(realtimeMessage.transactionId())) {
            handleTradeTick(realtimeMessage);
            return;
        }
        log.debug(
                "Unsupported KIS trade transactionId={}, body={}",
                realtimeMessage.transactionId(),
                realtimeMessage.body()
        );
    }

    private void handleTradeTick(ParsedRealtimeMessageDto realtimeMessage) {
        TickSnapshotEvent tradeTick = kisTradeTickParser.parse(
                realtimeMessage.body(), timeProvider.now()
        );

        tickEventProducer.publish(tradeTick);
    }

    private void handleJsonMessage(String message) {
        log.info("KIS trade websocket response={}", message);
    }
}
