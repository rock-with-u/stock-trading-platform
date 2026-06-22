package com.isyoudwn.account_service.order.infrastructure.repository;

import com.isyoudwn.account_service.order.domain.StockOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockOrderRepository extends JpaRepository<StockOrder, Long> {
    boolean existsByIdempotencyKey(String idempotencyKey);
}
