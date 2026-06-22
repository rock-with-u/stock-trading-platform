package com.isyoudwn.market_service.common.websocket;

import com.isyoudwn.market_service.quote.infrastructure.websocket.KisQuoteWebSocketMessageHandler;
import com.isyoudwn.market_service.trade.infrastructure.websocket.KisTradeWebSocketMessageHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KisRealtimeWebSocketMessageDispatcher {

    private final KisRealtimeMessageParser realtimeMessageParser;
    private final KisQuoteWebSocketMessageHandler quoteMessageHandler;
    private final KisTradeWebSocketMessageHandler tradeMessageHandler;

    public void dispatch(String message) {
        if (message == null || message.isBlank()) {
            return;
        }

        if (message.startsWith("{")) {
            return;
        }

        ParsedRealtimeMessageDto realtimeMessage = realtimeMessageParser.parse(message);

        String transactionId = realtimeMessage.transactionId();

        if (KisRealtimeTransactionId.STOCK_QUOTE.getCode().equals(transactionId)) {
            quoteMessageHandler.handleMessage(message);
            return;
        }

        if (KisRealtimeTransactionId.STOCK_TRADE.getCode().equals(transactionId)) {
            tradeMessageHandler.handleMessage(message);
        }
    }
}
