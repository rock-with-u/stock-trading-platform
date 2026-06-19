package com.isyoudwn.common_service.stock.service;

import com.isyoudwn.common_service.stock.domain.Stock;

public interface StockService {

    Stock getByStockCode(String stockCode);
}
