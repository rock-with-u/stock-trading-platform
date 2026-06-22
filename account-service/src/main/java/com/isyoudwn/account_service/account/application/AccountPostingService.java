package com.isyoudwn.account_service.account.application;

import com.isyoudwn.account_service.account.domain.Account;
import com.isyoudwn.account_service.account.domain.AccountPosting;
import java.time.LocalDateTime;

public interface AccountPostingService {

    boolean existsByIdempotencyKey(String idempotencyKey);

    AccountPosting createOrderReservationReleasePosting(
            Account account,
            Long rejectOrderId,
            String idempotencyKey,
            LocalDateTime rejectedAt);
}
