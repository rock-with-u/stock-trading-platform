package com.isyoudwn.account_service.account.infrastructure;

import com.isyoudwn.account_service.account.domain.Account;
import com.isyoudwn.account_service.account.domain.StockPosition;
import com.isyoudwn.common_service.stock.domain.Stock;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockPositionRepository extends JpaRepository<StockPosition, Long> {

    Optional<StockPosition> findByAccountAndStock(Account account, Stock stock);
}
