package com.isyoudwn.account_service.account.infrastructure;

import com.isyoudwn.account_service.account.domain.StockPositionChange;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockPositionChangeRepository extends JpaRepository<StockPositionChange, Long> {
}
