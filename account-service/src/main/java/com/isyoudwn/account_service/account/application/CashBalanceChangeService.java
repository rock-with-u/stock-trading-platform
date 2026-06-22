package com.isyoudwn.account_service.account.application;

import com.isyoudwn.account_service.account.application.dto.OrderReservationReleaseResult;
import com.isyoudwn.account_service.account.domain.AccountPosting;
import com.isyoudwn.account_service.account.domain.CashBalanceChange;
import com.isyoudwn.account_service.account.domain.CashBucket;
import com.isyoudwn.account_service.account.domain.dto.BuyReservationResult;
import com.isyoudwn.account_service.account.infrastructure.CashBalanceChangeRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CashBalanceChangeService {

    private final CashBalanceChangeRepository cashBalanceChangeRepository;

    public void writeBuyReservation(
            AccountPosting posting,
            long settledBefore,
            long unsettledBefore,
            long reservedBefore,
            BuyReservationResult result
    ) {
        long unsettledAfter = unsettledBefore - result.usedUnsettledSellReceivableAmount();
        long settledAfter = settledBefore - result.usedSettledCashAmount();
        long reservedAfter = reservedBefore + result.reservedBuyAmount();

        if (result.usedUnsettledSellReceivableAmount() > 0) {
            cashBalanceChangeRepository.save(
                    CashBalanceChange.create(
                            posting,
                            CashBucket.UNSETTLED_SELL_RECEIVABLE,
                            unsettledBefore,
                            unsettledAfter
                    )
            );
        }

        if (result.usedSettledCashAmount() > 0) {
            cashBalanceChangeRepository.save(
                    CashBalanceChange.create(
                            posting,
                            CashBucket.SETTLED_CASH,
                            settledBefore,
                            settledAfter
                    )
            );
        }

        cashBalanceChangeRepository.save(
                CashBalanceChange.create(
                        posting,
                        CashBucket.RESERVED_BUY,
                        reservedBefore,
                        reservedAfter
                )
        );
    }

    public void writeBuyReservationRelease(
            AccountPosting posting,
            long settledBefore,
            long unsettledBefore,
            long reservedBefore,
            OrderReservationReleaseResult.Buy result
    ) {
        if (result.restoredSettledAmount() > 0) {
            cashBalanceChangeRepository.save(
                    CashBalanceChange.create(
                            posting,
                            CashBucket.SETTLED_CASH,
                            settledBefore,
                            settledBefore + result.restoredSettledAmount()
                    )
            );
        }

        if (result.restoredUnsettledAmount() > 0) {
            cashBalanceChangeRepository.save(
                    CashBalanceChange.create(
                            posting,
                            CashBucket.UNSETTLED_SELL_RECEIVABLE,
                            unsettledBefore,
                            unsettledBefore + result.restoredUnsettledAmount()
                    )
            );
        }

        if (result.releasedReservedAmount() > 0) {
            cashBalanceChangeRepository.save(
                    CashBalanceChange.create(
                            posting,
                            CashBucket.RESERVED_BUY,
                            reservedBefore,
                            reservedBefore - result.releasedReservedAmount()
                    )
            );
        }
    }

    public OrderReservationReleaseResult.Buy calculateBuyReservationRelease(Long parentOrderId) {
        List<CashBalanceChange> reservationChanges =
                cashBalanceChangeRepository.findBuyReservationChanges(parentOrderId);

        long usedSettledAmount = sumDecreasedCashAmount(reservationChanges,
                CashBucket.SETTLED_CASH);
        long usedUnsettledAmount = sumDecreasedCashAmount(reservationChanges,
                CashBucket.UNSETTLED_SELL_RECEIVABLE);
        long reservedAmount = sumIncreasedCashAmount(reservationChanges,
                CashBucket.RESERVED_BUY);

        return OrderReservationReleaseResult.Buy.of(usedSettledAmount, usedUnsettledAmount, reservedAmount);
    }

    private long sumDecreasedCashAmount(List<CashBalanceChange> changes, CashBucket cashBucket) {
        long totalAmount = 0L;

        for (CashBalanceChange change : changes) {
            if (change.getCashBucket() != cashBucket) {
                continue;
            }

            long decreasedAmount = change.getAmountBefore() - change.getAmountAfter();
            totalAmount += decreasedAmount;
        }

        return totalAmount;
    }

    private long sumIncreasedCashAmount(List<CashBalanceChange> changes, CashBucket cashBucket) {
        long totalAmount = 0L;

        for (CashBalanceChange change : changes) {
            if (change.getCashBucket() != cashBucket) {
                continue;
            }

            long increasedAmount = change.getAmountAfter() - change.getAmountBefore();

            totalAmount += increasedAmount;
        }

        return totalAmount;
    }
}
