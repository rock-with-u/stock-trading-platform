package com.isyoudwn.account_service.account.infrastructure;

import com.isyoudwn.account_service.account.domain.CashBalanceChange;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CashBalanceChangeRepository extends JpaRepository<CashBalanceChange, Long> {
}
