package com.isyoudwn.common_service.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResponseMessage {
    SUCCESS("SUCCESS-001", "요청을 성공척으로 처리했습니다"),
    FAIL("FAIL-001", "요청을 실패했습니다"),

    INVALID_ACCOUNT_STATUS("ACCOUNT-ERROR-001", "올바르지 않는 계좌 상태입니다."),
    INVALID_POSTING_STATUS("ACCOUNT-ERROR-002", "지원하지 않는 전표 상태입니다."),
    INVALID_CASH_BUCKET("ACCOUNT-ERROR-003", "지원하지 않는 금액 상태입니다."),
    INVALID_POSTING_SOURCE_TYPE("ACCOUNT-ERROR-005", "지원하지 않는 소스 타입입니다."),
    INVALID_CASH_TRANSACTION_TYPE("ACCOUNT-ERROR-004", "지원하지 않는 입출금 상태입니다."),

    INVALID_MEMBER_STATUS("MEMBER-ERROR-001", "올바르지 않은 회원 상태입니다."),
    INVALID_POSITION_BUCKET("ACCOUNT-ERROR-007", "올바르지 않은 보유 주식 변경입니다."),

    INVALID_CASH_TRANSACTION_AMOUNT("ACCOUNT-ERROR-006", "입출금 금액은 0보다 커야 합니다."),

    INVALID_TRADE_EXECUTION_QUANTITY("TRADE-EXECUTION-001", "체결수량은 0보다 커야합니다."),
    INVALID_TRADE_EXECUTION_UNIT_PRICE("TRADE-EXECUTION-002", "체결 단가는 0보다 커야 합니다.");


    private final String code;
    private final String message;
}
