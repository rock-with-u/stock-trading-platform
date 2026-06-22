package com.isyoudwn.market_service.common.websocket;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KisRealtimeWebSocketLifecycleManager {

    private final KisRealtimeSubscriptionService realtimeSubscriptionService;

    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        realtimeSubscriptionService
                .start()
                .whenComplete((ignored, throwable) -> {
                    if (throwable == null) {
                        log.info("KIS 실시간 웹소켓 초기화 완료");
                        return;
                    }

                    log.error("KIS 실시간 웹소켓 초기화 실패", throwable);
                });
    }

    @PreDestroy
    public void stop() {
        try {
            realtimeSubscriptionService
                    .stop()
                    .join();

            log.info("KIS 실시간 웹소켓 종료 완료");

        } catch (RuntimeException exception) {
            log.warn("KIS 실시간 웹소켓 종료 중 오류 발생", exception);
        }
    }
}
