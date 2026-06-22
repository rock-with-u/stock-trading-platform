package com.isyoudwn.account_service.account.application;

import com.isyoudwn.account_service.account.domain.Account;
import com.isyoudwn.account_service.account.domain.AccountPosting;
import com.isyoudwn.account_service.account.domain.PostingSourceType;
import com.isyoudwn.account_service.account.domain.PostingType;
import com.isyoudwn.account_service.account.infrastructure.AccountPostingRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountPostingServiceImpl implements AccountPostingService {
    private final AccountPostingRepository accountPostingRepository;

    @Override
    public boolean existsByIdempotencyKey(String idempotencyKey) {
        return accountPostingRepository.existsByIdempotencyKey(idempotencyKey);
    }

    public AccountPosting createOrderReservationReleasePosting(
            Account account,
            Long rejectOrderId,
            String idempotencyKey,
            LocalDateTime rejectedAt
    ) {
        AccountPosting posting = AccountPosting.create(
                account,
                PostingType.ORDER_REJECT,
                rejectOrderId,
                PostingSourceType.STOCK_ORDER,
                idempotencyKey,
                rejectedAt
        );

        return accountPostingRepository.save(posting);
    }
}
