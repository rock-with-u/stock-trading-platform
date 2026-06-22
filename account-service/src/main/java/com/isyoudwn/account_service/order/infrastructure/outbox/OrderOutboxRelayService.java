package com.isyoudwn.account_service.order.infrastructure.outbox;

import com.isyoudwn.account_service.order.infrastructure.outbox.rabbitmq.RabbitOrderOutboxEventPublisher;
import com.isyoudwn.account_service.order.infrastructure.repository.OrderOutboxEventRepository;
import com.isyoudwn.common_service.config.TimeProvider;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderOutboxRelayService {

    private final OrderOutboxEventRepository orderOutboxEventRepository;
    private final OrderOutboxMarkService orderOutboxMarkService;
    private final RabbitOrderOutboxEventPublisher publisher;
    private final TimeProvider timeProvider;

    public void publishEvents() {

        List<OrderOutboxEvent> events = orderOutboxEventRepository.findPublishableEvents(
                List.of(
                        OrderOutboxEventStatus.PENDING,
                        OrderOutboxEventStatus.FAILED
                ),
                timeProvider.now(),
                PageRequest.of(0, 100)
        );

        for (OrderOutboxEvent event : events) {
            try {
                publisher.publish(event);
                orderOutboxMarkService.markPublished(event.getEventId(), LocalDateTime.now());

            } catch (Exception ex) {
                orderOutboxMarkService.markFailed(event.getEventId(), ex.getMessage(), LocalDateTime.now());
            }
        }
    }
}
