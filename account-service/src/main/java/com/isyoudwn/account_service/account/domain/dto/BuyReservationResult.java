package com.isyoudwn.account_service.account.domain.dto;

public record BuyReservationResult(
        long usedSettledCashAmount,
        long usedUnsettledSellReceivableAmount,
        long reservedBuyAmount
) {
}
