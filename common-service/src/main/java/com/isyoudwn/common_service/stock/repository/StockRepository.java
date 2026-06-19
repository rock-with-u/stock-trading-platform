package com.isyoudwn.common_service.stock.repository;

import com.isyoudwn.common_service.stock.domain.Stock;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock, Long> {
    Optional<Stock> findByCode(String code);
}
