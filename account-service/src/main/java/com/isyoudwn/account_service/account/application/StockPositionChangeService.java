package com.isyoudwn.account_service.account.application;

import com.isyoudwn.account_service.account.domain.AccountPosting;
import com.isyoudwn.account_service.account.domain.PositionBucket;
import com.isyoudwn.account_service.account.domain.StockPosition;
import com.isyoudwn.account_service.account.domain.StockPositionChange;
import com.isyoudwn.account_service.account.domain.dto.SellReservationResult;
import com.isyoudwn.account_service.account.infrastructure.StockPositionChangeRepository;
import com.isyoudwn.common_service.stock.domain.Stock;
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
                        result.quantityBefore(),
                        result.quantityAfter(),
                        stockPosition.getAveragePrice(),
                        stockPosition.getAveragePrice()
                )
        );
    }
}
