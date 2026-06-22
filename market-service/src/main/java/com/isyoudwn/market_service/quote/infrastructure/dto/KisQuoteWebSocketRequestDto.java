package com.isyoudwn.market_service.quote.infrastructure.dto;

import com.isyoudwn.market_service.common.KisRealtimeTransactionId;
import com.isyoudwn.market_service.common.KisWebSocketRequestDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KisQuoteWebSocketRequestDto {

    public static KisWebSocketRequestDto.Request subscribe(
            String approvalKey,
            String stockCode
    ) {
        return KisWebSocketRequestDto.subscribe(
                approvalKey,
                KisRealtimeTransactionId.STOCK_QUOTE,
                stockCode
        );
    }

    public static KisWebSocketRequestDto.Request unsubscribe(
            String approvalKey,
            String stockCode
    ) {
        return KisWebSocketRequestDto.unsubscribe(
                approvalKey,
                KisRealtimeTransactionId.STOCK_QUOTE,
                stockCode
        );
    }
}
