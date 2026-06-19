package com.isyoudwn.common_service.stock.service;

import com.isyoudwn.common_service.stock.domain.Stock;
import java.util.List;

public interface StockService {

    Stock getByStockCode(String stockCode);
    List<String> getStockCodes();
}
