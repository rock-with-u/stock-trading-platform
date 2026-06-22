package com.isyoudwn.account_service.order.infrastructure.outbox;

public enum OrderOutboxEventType {
    LIMIT_ORDER_CREATED, // 주문 생성
    LIMIT_ORDER_CANCEL_REQUESTED, // 주문 취소
    LIMIT_ORDER_AMEND_REQUESTED // 주문 정정
}
