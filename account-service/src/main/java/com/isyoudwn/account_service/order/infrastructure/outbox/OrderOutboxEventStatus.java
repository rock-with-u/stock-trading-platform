package com.isyoudwn.account_service.order.infrastructure.outbox;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum OrderOutboxEventStatus {
    PENDING("발행 대기"),
    PUBLISHED("발행 완료"),
    FAILED("발행 실패"),
    DEAD_LETTERED("최대 재시도 초과");

    private final String description;
}
