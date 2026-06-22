package com.isyoudwn.account_service.account.application;

import com.isyoudwn.account_service.account.domain.Account;
import com.isyoudwn.account_service.account.domain.StockPosition;
import com.isyoudwn.account_service.account.infrastructure.StockPositionRepository;
import com.isyoudwn.common_service.exception.AccountException;
import com.isyoudwn.common_service.response.ResponseMessage;
import com.isyoudwn.common_service.stock.domain.Stock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockPositionService {
    private final StockPositionRepository stockPositionRepository;

    public StockPosition getByAccount(Account account, Stock stock) {
        return stockPositionRepository
                .findByAccountAndStock(account, stock)
                .orElseThrow(() -> new AccountException(ResponseMessage.STOCK_POSITION_NOT_FOUND));
    }

    public StockPosition getByAccountAndStock(Account account, Stock stock) {
        return stockPositionRepository
                .findByAccountAndStock(account, stock)
                .orElseThrow(() -> new AccountException(ResponseMessage.STOCK_POSITION_NOT_FOUND));
    }
}
