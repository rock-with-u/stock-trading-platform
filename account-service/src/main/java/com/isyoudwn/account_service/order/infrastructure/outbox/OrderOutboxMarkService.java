package com.isyoudwn.account_service.order.infrastructure.outbox;

import com.isyoudwn.account_service.order.application.OrderRejectService;
import com.isyoudwn.account_service.order.infrastructure.repository.OrderOutboxEventRepository;
import com.isyoudwn.common_service.exception.OrderOutboxException;
import com.isyoudwn.common_service.response.ResponseMessage;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderOutboxMarkService {

    private final OrderOutboxEventRepository orderOutboxEventRepository;
    private final OrderRejectService orderRejectService;

    @Transactional
    public void markPublished(String eventId, LocalDateTime now) {
        OrderOutboxEvent event = orderOutboxEventRepository
                .findByEventId(eventId)
                .orElseThrow(
                        () -> new OrderOutboxException(ResponseMessage.ORDER_OUTBOX_EVENT_IS_NOT_EXIST)
                );

        event.markPublished(now);
    }

    @Transactional
    public void markFailed(String eventId, String reason, LocalDateTime now) {
        OrderOutboxEvent event = orderOutboxEventRepository
                .findByEventId(eventId)
                .orElseThrow(
                        () -> new OrderOutboxException(ResponseMessage.ORDER_OUTBOX_EVENT_IS_NOT_EXIST)
                );

        event.markFailed(reason, now);

        if (event.getStatus() == OrderOutboxEventStatus.DEAD_LETTERED) {
            orderRejectService.rejectOrder(event.getStockOrderId());
        }
    }
}
