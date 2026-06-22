package com.isyoudwn.account_service.account.application;

import com.isyoudwn.account_service.account.application.dto.OrderReservationReleaseResult;
import com.isyoudwn.account_service.account.domain.AccountPosting;
import com.isyoudwn.account_service.account.domain.PositionBucket;
import com.isyoudwn.account_service.account.domain.StockPosition;
import com.isyoudwn.account_service.account.domain.StockPositionChange;
import com.isyoudwn.account_service.account.domain.dto.SellReservationResult;
import com.isyoudwn.account_service.account.infrastructure.StockPositionChangeRepository;
import com.isyoudwn.common_service.stock.domain.Stock;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockPositionChangeService {

    private final StockPositionChangeRepository stockPositionChangeRepository;

    public void writeSellReservation(
            AccountPosting posting,
            Stock stock,
            StockPosition stockPosition,
            SellReservationResult result
    ) {
        stockPositionChangeRepository.save(
                StockPositionChange.create(
                        posting,
                        stock,
                        PositionBucket.SELL_RESERVED_QUANTITY,
                        result.sellReservedQuantityBefore(),
                        result.sellReservedQuantityAfter(),
                        stockPosition.getAveragePrice(),
                        stockPosition.getAveragePrice()
                )
        );
    }

    public OrderReservationReleaseResult.Sell calculateSellReservationRelease(Long parentOrderId) {
        List<StockPositionChange> reservationChanges =
                stockPositionChangeRepository.findSellReservationChanges(parentOrderId);

        long releasedReservedQuantity = sumIncreasedPositionQuantity(
                reservationChanges,
                PositionBucket.SELL_RESERVED_QUANTITY
        );

        return OrderReservationReleaseResult.Sell.of(releasedReservedQuantity);
    }

    public void writeSellReservationRelease(
            AccountPosting posting,
            Stock stock,
            long sellReservedBefore,
            long averagePrice,
            OrderReservationReleaseResult.Sell result
    ) {
        long releasedReservedQuantity = result.releasedReservedQuantity();

        stockPositionChangeRepository.save(
                StockPositionChange.create(
                        posting,
                        stock,
                        PositionBucket.SELL_RESERVED_QUANTITY,
                        sellReservedBefore,
                        sellReservedBefore - releasedReservedQuantity,
                        averagePrice,
                        averagePrice
                )
        );
    }

    private long sumIncreasedPositionQuantity(List<StockPositionChange> changes, PositionBucket positionBucket) {
        long totalQuantity = 0L;

        for (StockPositionChange change : changes) {
            if (change.getPositionBucket() != positionBucket) {
                continue;
            }

            long deltaQuantity = change.getDeltaQuantity();

            if (deltaQuantity <= 0) {
                continue;
            }

            totalQuantity += deltaQuantity;
        }

        return totalQuantity;
    }
}
