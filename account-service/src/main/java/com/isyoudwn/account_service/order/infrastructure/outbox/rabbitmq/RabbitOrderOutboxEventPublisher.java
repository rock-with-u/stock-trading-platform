package com.isyoudwn.account_service.order.infrastructure.outbox.rabbitmq;

import static com.isyoudwn.account_service.order.infrastructure.outbox.OrderOutboxEventType.LIMIT_ORDER_CREATED;

import com.isyoudwn.account_service.order.infrastructure.outbox.OrderOutboxEvent;
import com.isyoudwn.account_service.order.infrastructure.outbox.OrderOutboxEventType;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitOrderOutboxEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publish(OrderOutboxEvent event) {
        rabbitTemplate.convertAndSend(
                OrderOutboxRabbitConfig.ORDER_EXCHANGE,
                resolveRoutingKey(event.getEventType()),
                event.getPayload()
        );
    }

    private String resolveRoutingKey(OrderOutboxEventType eventType) {
        if (eventType == LIMIT_ORDER_CREATED) {
            return OrderOutboxRabbitConfig.LIMIT_ORDER_CREATED_ROUTING_KEY;
        }
        // TODO: 주문 정정, 주문 취소 구현
        return OrderOutboxRabbitConfig.LIMIT_ORDER_CREATED_ROUTING_KEY;
    }
}
