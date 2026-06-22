package com.isyoudwn.account_service.account.application.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderReservationReleaseResult {

    public record Buy(
            long restoredSettledAmount,
            long restoredUnsettledAmount,
            long releasedReservedAmount
    ) {
        public static Buy of(
                long restoredSettledAmount,
                long restoredUnsettledAmount,
                long releasedReservedAmount
        ) {
            return new Buy(
                    restoredSettledAmount,
                    restoredUnsettledAmount,
                    releasedReservedAmount
            );
        }
    }

    public record Sell(
            long releasedReservedQuantity
    ) {
        public static Sell of(long releasedReservedQuantity) {
            return new Sell(releasedReservedQuantity);
        }
    }
}
