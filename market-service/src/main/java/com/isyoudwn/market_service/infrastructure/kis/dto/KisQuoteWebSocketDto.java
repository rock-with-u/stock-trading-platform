package com.isyoudwn.market_service.infrastructure.kis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KisQuoteWebSocketDto {

    private static final String STOCK_ORDER_BOOK_TR_ID = "H0STASP0";
    private static final String CONTENT_TYPE = "utf-8";

    public record Request(
            Header header,
            Body body
    ) {

        public static Request subscribe(
                String approvalKey,
                String stockCode
        ) {
            return new Request(
                    Header.subscribe(approvalKey),
                    Body.stockOrderBook(stockCode)
            );
        }

        public static Request unsubscribe(
                String approvalKey,
                String stockCode
        ) {
            return new Request(
                    Header.unsubscribe(approvalKey),
                    Body.stockOrderBook(stockCode)
            );
        }
    }

    public record Header(
            @JsonProperty("approval_key")
            String approvalKey,

            @JsonProperty("custtype")
            String customerType,

            @JsonProperty("tr_type")
            String transactionType,

            @JsonProperty("content-type")
            String contentType
    ) {

        private static Header subscribe(String approvalKey) {
            return new Header(
                    approvalKey,
                    CustomerType.PERSONAL.code,
                    TransactionType.SUBSCRIBE.code,
                    CONTENT_TYPE
            );
        }

        private static Header unsubscribe(String approvalKey) {
            return new Header(
                    approvalKey,
                    CustomerType.PERSONAL.code,
                    TransactionType.UNSUBSCRIBE.code,
                    CONTENT_TYPE
            );
        }
    }

    public record Body(
            Input input
    ) {

        private static Body stockOrderBook(String stockCode) {
            return new Body(
                    new Input(
                            STOCK_ORDER_BOOK_TR_ID,
                            stockCode
                    )
            );
        }
    }

    public record Input(
            @JsonProperty("tr_id")
            String transactionId,

            @JsonProperty("tr_key")
            String transactionKey
    ) {
    }

    private enum CustomerType {

        CORPORATE("B"),
        PERSONAL("P");

        private final String code;

        CustomerType(String code) {
            this.code = code;
        }
    }

    private enum TransactionType {

        SUBSCRIBE("1"),
        UNSUBSCRIBE("2");

        private final String code;

        TransactionType(String code) {
            this.code = code;
        }
    }
}
