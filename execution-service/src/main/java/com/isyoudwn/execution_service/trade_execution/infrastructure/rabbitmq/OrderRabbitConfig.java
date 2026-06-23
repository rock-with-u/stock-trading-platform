package com.isyoudwn.execution_service.trade_execution.infrastructure.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderRabbitConfig {

    public static final String ORDER_EXCHANGE = "order.exchange";

    public static final String LIMIT_ORDER_CREATED_QUEUE =
            "execution.limit-order-created.queue";

    public static final String LIMIT_ORDER_CREATED_ROUTING_KEY =
            "order.limit.created";

    @Bean
    public DirectExchange orderExchange() {
        return ExchangeBuilder
                .directExchange(ORDER_EXCHANGE)
                .durable(true)
                .build();
    }

    @Bean
    public Queue limitOrderCreatedQueue() {
        return QueueBuilder
                .durable(LIMIT_ORDER_CREATED_QUEUE)
                .build();
    }

    @Bean
    public Binding limitOrderCreatedBinding() {
        return BindingBuilder
                .bind(limitOrderCreatedQueue())
                .to(orderExchange())
                .with(LIMIT_ORDER_CREATED_ROUTING_KEY);
    }
}
