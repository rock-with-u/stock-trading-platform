package com.isyoudwn.account_service.account.application;

import com.isyoudwn.account_service.account.domain.Account;
import com.isyoudwn.account_service.account.infrastructure.AccountRepository;
import com.isyoudwn.common_service.exception.AccountException;
import com.isyoudwn.common_service.response.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    public Account getByAccountNumber(String accountNumber) {
        return accountRepository
                .findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ResponseMessage.ACCOUNT_NOT_FOUND));
    }
}
