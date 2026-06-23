package com.isyoudwn.execution_service.trade_execution.infrastructure.rabbitmq;

import com.isyoudwn.execution_service.trade_execution.application.LimitOrderCreatedCommand;
import com.isyoudwn.execution_service.trade_execution.application.LimitOrderExecutionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class LimitOrderCreatedConsumer {

    private final ObjectMapper objectMapper;
    private final LimitOrderExecutionService limitOrderExecutionService;

    @RabbitListener(queues = OrderRabbitConfig.LIMIT_ORDER_CREATED_QUEUE)
    public void consume(String payload) {
        try {
            LimitOrderCreatedMessage message = objectMapper.readValue(payload, LimitOrderCreatedMessage.class);

            log.info(
                    "지정가 주문 생성 메시지 수신. orderId={}, accountNumber={}, stockCode={}, side={}, quantity={}, limitPrice={}",
                    message.orderId(),
                    message.accountNumber(),
                    message.stockCode(),
                    message.orderSide(),
                    message.orderQuantity(),
                    message.limitPrice()
            );

            LimitOrderCreatedCommand command = new LimitOrderCreatedCommand(
                    message.orderId(),
                    message.accountNumber(),
                    message.stockCode(),
                    message.orderSide(),
                    message.orderQuantity(),
                    message.limitPrice(),
                    message.orderedAt()
            );

            limitOrderExecutionService.register(command);

        } catch (Exception e) {
            log.error("지정가 주문 생성 메시지 처리 실패. payload={}", payload, e);
            throw new IllegalStateException("지정가 주문 생성 메시지 처리 실패", e);
        }
    }
}
