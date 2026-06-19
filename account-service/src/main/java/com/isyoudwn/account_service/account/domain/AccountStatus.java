package com.isyoudwn.account_service.account.domain;

import com.isyoudwn.common_service.exception.AccountException;
import com.isyoudwn.common_service.response.ResponseMessage;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum AccountStatus {
    ENABLE("활성화"),
    DISABLE("비활성화");

    private final String description;

    public static AccountStatus of(String status) {
        if (status == null || status.isBlank()) {
            throw new AccountException(ResponseMessage.INVALID_ACCOUNT_STATUS);
        }

        for (AccountStatus accountStatus : values()) {
            if (status.equals(accountStatus.name())) {
                return accountStatus;
            }
        }

        throw new AccountException(ResponseMessage.INVALID_ACCOUNT_STATUS);
    }
}

