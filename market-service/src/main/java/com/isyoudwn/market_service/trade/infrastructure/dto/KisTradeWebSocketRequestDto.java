package com.isyoudwn.market_service.trade.infrastructure.dto;

import com.isyoudwn.market_service.common.websocket.KisRealtimeTransactionId;
import com.isyoudwn.market_service.common.websocket.KisWebSocketRequestDto;

public class KisTradeWebSocketRequestDto {

    public static KisWebSocketRequestDto.Request subscribe(
            String approvalKey,
            String stockCode
    ) {
        return KisWebSocketRequestDto.subscribe(
                approvalKey,
                KisRealtimeTransactionId.STOCK_TRADE,
                stockCode
        );
    }

    public static KisWebSocketRequestDto.Request unsubscribe(
            String approvalKey,
            String stockCode
    ) {
        return KisWebSocketRequestDto.unsubscribe(
                approvalKey,
                KisRealtimeTransactionId.STOCK_TRADE,
                stockCode
        );
    }
}
