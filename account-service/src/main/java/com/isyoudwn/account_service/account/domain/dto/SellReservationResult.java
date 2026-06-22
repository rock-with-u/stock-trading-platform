package com.isyoudwn.account_service.account.domain.dto;

public record SellReservationResult(
        long sellReservedQuantityBefore,
        long sellReservedQuantityAfter
) {
    public static SellReservationResult of(
            long sellReservedQuantityBefore,
            long sellReservedQuantityAfter
    ) {
        return new SellReservationResult(
                sellReservedQuantityBefore,
                sellReservedQuantityAfter
        );
    }
}
