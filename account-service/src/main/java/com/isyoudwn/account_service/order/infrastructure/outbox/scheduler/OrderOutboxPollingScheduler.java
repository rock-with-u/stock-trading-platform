package com.isyoudwn.account_service.order.infrastructure.outbox.scheduler;

import com.isyoudwn.account_service.order.infrastructure.outbox.OrderOutboxRelayService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderOutboxPollingScheduler {

    private final OrderOutboxRelayService orderOutboxRelayService;

    @Scheduled(fixedDelay = 2000)
    public void publishEvents() {
        orderOutboxRelayService.publishEvents();
    }
}
