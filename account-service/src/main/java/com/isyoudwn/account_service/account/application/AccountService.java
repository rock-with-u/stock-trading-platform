package com.isyoudwn.account_service.account.application;

import com.isyoudwn.account_service.account.domain.Account;

public interface AccountService {

    Account getByAccountNumber(String accountNumber);
}
