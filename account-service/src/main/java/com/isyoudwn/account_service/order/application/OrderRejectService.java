package com.isyoudwn.account_service.order.application;

import com.isyoudwn.account_service.account.application.AccountPostingService;
import com.isyoudwn.account_service.account.application.CashBalanceChangeService;
import com.isyoudwn.account_service.account.application.StockPositionChangeService;
import com.isyoudwn.account_service.account.application.StockPositionService;
import com.isyoudwn.account_service.account.application.dto.OrderReservationReleaseResult;
import com.isyoudwn.account_service.account.domain.Account;
import com.isyoudwn.account_service.account.domain.AccountPosting;
import com.isyoudwn.account_service.account.domain.StockPosition;
import com.isyoudwn.account_service.order.domain.OrderSide;
import com.isyoudwn.account_service.order.domain.StockOrder;
import com.isyoudwn.common_service.config.TimeProvider;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class OrderRejectService {

    private final TimeProvider timeProvider;
    private final StockOrderService stockOrderService;
    private final OrderIdempotencyKeyFactory orderIdempotencyKeyFactory;
    private final CashBalanceChangeService cashBalanceChangeService;
    private final StockPositionChangeService stockPositionChangeService;
    private final AccountPostingService accountPostingService;
    private final StockPositionService stockPositionService;

    @Transactional
    public void rejectOrder(Long orderId) {
        StockOrder parentOrder = stockOrderService.getById(orderId);
        String idempotencyKey = orderIdempotencyKeyFactory.systemRejectOrderByOutboxDeadLetter(orderId);
        LocalDateTime rejectedAt = timeProvider.now();

        if (stockOrderService.existsByIdempotencyKey(idempotencyKey)) {
            return;
        }

        StockOrder rejectOrder = StockOrder.createSystemReject(
                parentOrder,
                idempotencyKey,
                rejectedAt
        );

        stockOrderService.save(rejectOrder);

        if (parentOrder.getOrderSide() == OrderSide.BUY) {
            releaseBuyReservation(parentOrder, rejectOrder, rejectedAt);
        } else {
            releaseSellReservation(parentOrder, rejectOrder, rejectedAt);
        }
    }

    private void releaseBuyReservation(StockOrder parentOrder, StockOrder rejectOrder, LocalDateTime rejectedAt) {
        String idempotencyKey = orderIdempotencyKeyFactory.orderReservationReleasePostingByRejectOrder(
                rejectOrder.getId());

        if (accountPostingService.existsByIdempotencyKey(idempotencyKey)) {
            return;
        }

        Account account = parentOrder.getAccount();
        OrderReservationReleaseResult.Buy releaseBuyResult = cashBalanceChangeService.calculateBuyReservationRelease(
                parentOrder.getId());

        long settledBefore = account.getAvailableSettledCashAmount();
        long unsettledBefore = account.getUnsettledSellReceivableAmount();
        long reservedBefore = account.getReservedBuyAmount();

        account.releaseBuyAmount(releaseBuyResult.restoredSettledAmount(), releaseBuyResult.restoredUnsettledAmount(),
                releaseBuyResult.releasedReservedAmount());

        AccountPosting accountPosting = accountPostingService.createOrderReservationReleasePosting(account,
                rejectOrder.getId(),
                idempotencyKey, rejectedAt);

        cashBalanceChangeService.writeBuyReservationRelease(
                accountPosting,
                settledBefore,
                unsettledBefore,
                reservedBefore,
                releaseBuyResult
        );
    }

    private void releaseSellReservation(StockOrder parentOrder, StockOrder rejectOrder, LocalDateTime rejectedAt) {
        String idempotencyKey = orderIdempotencyKeyFactory.orderReservationReleasePostingByRejectOrder(
                rejectOrder.getId());

        if (accountPostingService.existsByIdempotencyKey(idempotencyKey)) {
            return;
        }

        Account account = parentOrder.getAccount();
        StockPosition stockPosition = stockPositionService.getByAccountAndStock(account, parentOrder.getStock());

        OrderReservationReleaseResult.Sell releaseSellResult = stockPositionChangeService.calculateSellReservationRelease(
                parentOrder.getId());

        long sellReservedBefore = stockPosition.getSellReservedQuantity();
        long averagePrice = stockPosition.getAveragePrice();

        stockPosition.releaseSellReservation(releaseSellResult.releasedReservedQuantity());

        AccountPosting accountPosting = accountPostingService.createOrderReservationReleasePosting(account,
                rejectOrder.getId(),
                idempotencyKey, rejectedAt);

        stockPositionChangeService.writeSellReservationRelease(
                accountPosting,
                parentOrder.getStock(),
                sellReservedBefore,
                averagePrice,
                releaseSellResult
        );
    }
}
