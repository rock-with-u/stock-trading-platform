package com.isyoudwn.account_service.account.infrastructure;

import com.isyoudwn.account_service.order.infrastructure.outbox.rabbitmq.OrderOutboxRabbitConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderMessagePublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishOrderCreated(OrderCreatedMessage message) {
        rabbitTemplate.convertAndSend(
                OrderOutboxRabbitConfig.ORDER_EXCHANGE,
                OrderOutboxRabbitConfig.LIMIT_ORDER_CREATED_ROUTING_KEY,
                message
        );
    }
}
