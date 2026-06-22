package com.isyoudwn.market_service.quote.infrastructure.websocket;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class KisQuoteFieldIndex {

    public static final int MIN_FIELD_COUNT = 43;

    public static final int STOCK_CODE = 0;

    public static final int ASK_PRICE_START = 3;
    public static final int BID_PRICE_START = 13;

    public static final int ASK_QUANTITY_START = 23;
    public static final int BID_QUANTITY_START = 33;

    public static final int QUOTE_LEVEL_COUNT = 10;
}
