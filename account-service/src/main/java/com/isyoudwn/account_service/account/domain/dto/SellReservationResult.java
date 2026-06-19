package com.isyoudwn.account_service.account.domain.dto;

public record SellReservationResult(
        long quantityBefore,
        long quantityAfter,
        long deltaQuantity
) {
}
