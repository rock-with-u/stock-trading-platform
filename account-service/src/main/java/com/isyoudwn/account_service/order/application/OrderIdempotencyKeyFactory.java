package com.isyoudwn.account_service.order.application;

import java.time.LocalDate;
import org.springframework.stereotype.Component;

@Component
public class OrderIdempotencyKeyFactory {

    public String systemRejectOrderByOutboxDeadLetter(Long orderId) {
        return "ORDER:SYSTEM_REJECT:OUTBOX_DEAD_LETTER:ORDER:" + orderId;
    }

    public String orderReservationReleasePostingByRejectOrder(Long rejectOrderId) {
        return "ACCOUNT_POSTING:ORDER_RESERVATION_RELEASE:REJECT_ORDER:" + rejectOrderId;
    }
}
