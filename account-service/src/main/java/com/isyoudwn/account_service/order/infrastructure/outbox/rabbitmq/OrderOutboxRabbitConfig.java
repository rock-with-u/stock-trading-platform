package com.isyoudwn.account_service.order.infrastructure.outbox.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class OrderOutboxRabbitConfig {

    public static final String ORDER_EXCHANGE = "order.exchange";

    public static final String LIMIT_ORDER_QUEUE = "execution.limit-order.queue";
    public static final String LIMIT_ORDER_CREATED_ROUTING_KEY = "order.limit.created";

    @Bean
    public DirectExchange orderExchange() {
        return ExchangeBuilder
                .directExchange(ORDER_EXCHANGE)
                .durable(true)
                .build();
    }

    @Bean
    public Queue limitOrderQueue() {
        return QueueBuilder
                .durable(LIMIT_ORDER_QUEUE)
                .build();
    }

    @Bean
    public Binding limitOrderCreatedBinding() {
        return BindingBuilder
                .bind(limitOrderQueue())
                .to(orderExchange())
                .with(LIMIT_ORDER_CREATED_ROUTING_KEY);
    }
    
    // TODO: 주문정정, 주문 취소 구현
}
