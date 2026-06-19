package com.isyoudwn.common_service.stock.service;

import com.isyoudwn.common_service.exception.StockException;
import com.isyoudwn.common_service.response.ResponseMessage;
import com.isyoudwn.common_service.stock.domain.Stock;
import com.isyoudwn.common_service.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;

    @Override
    @Transactional(readOnly = true)
    public Stock getByStockCode(String stockCode) {
        return stockRepository
                .findByCode(stockCode)
                .orElseThrow(() -> new StockException(ResponseMessage.STOCK_NOT_FOUND));
    }
}
