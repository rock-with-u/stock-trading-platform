package com.isyoudwn.account_service.account.application;

import com.isyoudwn.account_service.account.domain.AccountPosting;
import com.isyoudwn.account_service.account.domain.CashBalanceChange;
import com.isyoudwn.account_service.account.domain.CashBucket;
import com.isyoudwn.account_service.account.domain.dto.BuyReservationResult;
import com.isyoudwn.account_service.account.infrastructure.CashBalanceChangeRepository;
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
}
