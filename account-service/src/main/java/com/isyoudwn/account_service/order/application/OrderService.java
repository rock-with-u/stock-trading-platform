package com.isyoudwn.account_service.order.application;

import com.isyoudwn.account_service.order.domain.StockOrder;
import com.isyoudwn.account_service.order.infrastructure.repository.StockOrderRepository;
import com.isyoudwn.common_service.exception.StockOrderException;
import com.isyoudwn.common_service.response.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final StockOrderRepository stockOrderRepository;

    public StockOrder getById(Long stockOrderId) {
        return stockOrderRepository.findById(stockOrderId)
                .orElseThrow(() -> new StockOrderException(ResponseMessage.ORDER_IS_NOT_EXISTED));
    }

    @Transactional
    public StockOrder save(StockOrder stockOrder) {
        return stockOrderRepository.save(stockOrder);
    }

    public boolean existsByIdempotencyKey(String idempotencyKey) {
        return stockOrderRepository.existsByIdempotencyKey(idempotencyKey);
    }
}
