package com.isyoudwn.market_service.trade.domain;

import com.isyoudwn.common_service.exception.MarketException;
import com.isyoudwn.common_service.response.ResponseMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CandleTimeframe {
    MINUTE_1("1m"),
    MINUTE_5("5m"),
    MINUTE_15("15m"),
    HOUR_1("1h"),
    HOUR_4("4h"),
    DAY_1("1d");

    private final String code;

    public static CandleTimeframe from(String code) {
        if (code == null || code.isBlank()) {
            throw new MarketException(ResponseMessage.INVALID_CANDLE_TIMEFRAME);
        }

        for (CandleTimeframe candleTimeframe : values()) {
            if (candleTimeframe.code.equals(code)) {
                return candleTimeframe;
            }
        }
        throw new MarketException(ResponseMessage.INVALID_CANDLE_TIMEFRAME);
    }
}
