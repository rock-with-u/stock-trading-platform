package com.isyoudwn.account_service.account.infrastructure;

import com.isyoudwn.account_service.account.domain.AccountPosting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountPostingRepository extends JpaRepository<AccountPosting, Long> {
    boolean existsByIdempotencyKey(String idempotencyKey);
}
