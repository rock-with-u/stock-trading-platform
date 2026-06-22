package com.isyoudwn.market_service.common.websocket;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum KisRealtimeTransactionId {

    STOCK_QUOTE("H0STASP0"),
    STOCK_TRADE("H0STCNT0");

    private final String code;
}
