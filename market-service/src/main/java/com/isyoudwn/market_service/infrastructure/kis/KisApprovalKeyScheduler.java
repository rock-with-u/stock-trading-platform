package com.isyoudwn.market_service.infrastructure.kis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KisApprovalKeyScheduler {

    private final KisApprovalKeyService kisApprovalKeyService;

    @EventListener(ApplicationReadyEvent.class)
    public void issueIfAbsentOnApplicationReady() {
        kisApprovalKeyService.issueIfAbsent();
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void refreshApprovalKey() {
        kisApprovalKeyService.issueAndSave();
    }
}
