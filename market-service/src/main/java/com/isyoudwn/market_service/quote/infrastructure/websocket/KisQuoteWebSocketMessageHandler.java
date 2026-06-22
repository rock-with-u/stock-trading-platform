package com.isyoudwn.market_service.quote.infrastructure.websocket;

import com.isyoudwn.common_service.config.TimeProvider;
import com.isyoudwn.market_service.common.websocket.KisRealtimeMessageParser;
import com.isyoudwn.market_service.common.websocket.KisRealtimeTransactionId;
import com.isyoudwn.market_service.common.websocket.ParsedRealtimeMessageDto;
import com.isyoudwn.market_service.quote.infrastructure.kafka.event.QuoteSnapshotEvent;
import com.isyoudwn.market_service.quote.infrastructure.kafka.producer.QuoteEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KisQuoteWebSocketMessageHandler {

    private final TimeProvider timeProvider;
    private final QuoteEventProducer quoteEventProducer;
    private final KisRealtimeMessageParser realtimeMessageParser;
    private final KisQuoteParser quoteParser;

    public void handleMessage(String message) {
        if (message == null || message.isBlank()) {
            return;
        }

        if (message.startsWith("{")) {
            handleJsonMessage(message);
            return;
        }

        ParsedRealtimeMessageDto realtimeMessage = realtimeMessageParser.parse(message);

        handleRealtimeMessage(realtimeMessage);
    }

    private void handleRealtimeMessage(ParsedRealtimeMessageDto realtimeMessage) {
        if (!realtimeMessage.isPlainText()) {
            log.warn(
                    "Encrypted KIS quote websocket message received. transactionId={}",
                    realtimeMessage.transactionId()
            );
            return;
        }

        if (KisRealtimeTransactionId.STOCK_QUOTE.getCode()
                .equals(realtimeMessage.transactionId())) {
            handleQuoteSnapshot(realtimeMessage);
            return;
        }

        log.debug(
                "Unsupported KIS quote transactionId={}, body={}",
                realtimeMessage.transactionId(),
                realtimeMessage.body()
        );
    }

    private void handleQuoteSnapshot(ParsedRealtimeMessageDto realtimeMessage) {
        QuoteSnapshotEvent event = quoteParser.parse(
                realtimeMessage.body(),
                timeProvider.now()
        );

        quoteEventProducer.publish(event);

        log.info(
                "KIS 실시간 호가 수신. dataCount={}, stockCode={}, askLevels={}, bidLevels={}, receivedAt={}",
                realtimeMessage.dataCount(),
                event.stockCode(),
                event.asks().size(),
                event.bids().size(),
                event.receivedAt()
        );
    }

    private void handleJsonMessage(String message) {
        log.info("KIS quote websocket response={}", message);
    }
}
