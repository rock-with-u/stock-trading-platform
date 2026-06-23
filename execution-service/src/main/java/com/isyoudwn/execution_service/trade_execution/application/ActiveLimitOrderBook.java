package com.isyoudwn.execution_service.trade_execution.application;

import com.isyoudwn.execution_service.trade_execution.domain.ActiveLimitOrder;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ActiveLimitOrderBook {

    private final Map<Long, ActiveLimitOrder> orders = new ConcurrentHashMap<>();

    public boolean add(ActiveLimitOrder order) {
        ActiveLimitOrder previous = orders.putIfAbsent(order.orderId(), order);

        if (previous != null) {
            log.info("이미 활성 주문북에 등록된 주문입니다. orderId={}", order.orderId());
            return false;
        }

        log.info(
                "활성 지정가 주문 등록 완료. orderId={}, stockCode={}, side={}, remainingQuantity={}, limitPrice={}",
                order.orderId(),
                order.stockCode(),
                order.orderSide(),
                order.remainingQuantity(),
                order.limitPrice()
        );

        return true;
    }

    public void remove(Long orderId) {
        ActiveLimitOrder removed = orders.remove(orderId);

        if (removed != null) {
            log.info("활성 지정가 주문 제거 완료. orderId={}", orderId);
        }
    }

    public List<ActiveLimitOrder> findByStockCode(String stockCode) {
        return orders.values()
                .stream()
                .filter(order -> order.stockCode().equals(stockCode))
                .toList();
    }

    public List<ActiveLimitOrder> findAll() {
        return new ArrayList<>(orders.values());
    }

    public int size() {
        return orders.size();
    }
}
