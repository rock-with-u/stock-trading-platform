package com.isyoudwn.execution_service.trade_execution.application;

import com.isyoudwn.execution_service.trade_execution.domain.ActiveLimitOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LimitOrderExecutionService {

    private final ActiveLimitOrderBook activeLimitOrderBook;

    public void register(LimitOrderCreatedCommand command) {
        validate(command);

        ActiveLimitOrder activeOrder = new ActiveLimitOrder(
                command.orderId(),
                command.accountNumber(),
                command.stockCode(),
                command.orderSide(),
                command.orderQuantity(),
                command.limitPrice(),
                command.orderedAt()
        );

        boolean registered = activeLimitOrderBook.add(activeOrder);

        if (!registered) {
            return;
        }

        log.info(
                "지정가 주문 체결계 접수 완료. activeOrderCount={}",
                activeLimitOrderBook.size()
        );

        // TODO: 이후 현재가/호가 기준으로 즉시 체결 가능 여부 판단
    }

    private void validate(LimitOrderCreatedCommand command) {
        if (command.orderId() == null) {
            throw new IllegalArgumentException("orderId는 필수입니다.");
        }

        if (command.accountNumber() == null || command.accountNumber().isBlank()) {
            throw new IllegalArgumentException("accountNumber는 필수입니다.");
        }

        if (command.stockCode() == null || command.stockCode().isBlank()) {
            throw new IllegalArgumentException("stockCode는 필수입니다.");
        }

        if (command.orderSide() == null || command.orderSide().isBlank()) {
            throw new IllegalArgumentException("orderSide는 필수입니다.");
        }

        if (!command.orderSide().equals("BUY") && !command.orderSide().equals("SELL")) {
            throw new IllegalArgumentException("지원하지 않는 주문 방향입니다. orderSide=" + command.orderSide());
        }

        if (command.orderQuantity() <= 0) {
            throw new IllegalArgumentException("orderQuantity는 1 이상이어야 합니다.");
        }

        if (command.limitPrice() <= 0) {
            throw new IllegalArgumentException("limitPrice는 1 이상이어야 합니다.");
        }

        if (command.orderedAt() == null) {
            throw new IllegalArgumentException("orderedAt은 필수입니다.");
        }
    }
}
