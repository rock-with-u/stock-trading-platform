package com.isyoudwn.account_service.domain.member;

import com.isyoudwn.common_service.exception.MemberException;
import com.isyoudwn.common_service.response.ResponseMessage;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum MemberStatus {
    ENABLE("활성화"),
    DISABLE("비활성화");

    private final String description;

    public static MemberStatus of(String status) {
        if (status == null || status.isBlank()) {
            throw new MemberException(ResponseMessage.INVALID_MEMBER_STATUS);
        }

        for (MemberStatus memberStatus : values()) {
            if (status.equals(memberStatus.name())) {
                return memberStatus;
            }
        }

        throw new MemberException(ResponseMessage.INVALID_POSTING_SOURCE_TYPE);
    }
}
