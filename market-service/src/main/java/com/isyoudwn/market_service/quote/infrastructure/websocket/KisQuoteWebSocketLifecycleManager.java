package com.isyoudwn.market_service.quote.infrastructure.websocket;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KisQuoteWebSocketLifecycleManager {

    private final KisQuoteSubscriptionService subscriptionService;

    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        subscriptionService
                .start()
                .whenComplete((ignored, throwable) -> {
                    if (throwable == null) {
                        log.info("KIS 호가 웹소켓 초기화 완료");
                        return;
                    }

                    log.error("KIS 호가 웹소켓 초기화 실패", throwable);
                });
    }

    @PreDestroy
    public void stop() {
        try {
            subscriptionService
                    .stop()
                    .join();
        } catch (RuntimeException exception) {
            log.warn("KIS 호가 웹소켓 종료 중 오류 발생", exception);
        }
    }
}
