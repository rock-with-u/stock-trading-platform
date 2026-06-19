package com.isyoudwn.market_service.infrastructure.kis.websocket;

import com.isyoudwn.common_service.stock.repository.StockRepository;
import com.isyoudwn.market_service.infrastructure.kis.KisApprovalKeyService;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KisQuoteSubscriptionService {

    private final StockRepository stockRepository;
    private final KisApprovalKeyService approvalKeyService;
    private final KisQuoteWebSocketClient webSocketClient;

    public CompletableFuture<Void> start() {
//        Stock stock = stockRepository
//                .findByCode()
//                .orElseThrow(() ->
//                        new IllegalStateException(
//                                "실시간 호가를 구독할 종목이 없습니다."
//                        )
//                );

        String approvalKey =
                approvalKeyService.getOrIssue();

        String stockCode = "005930";
//                stock.getStockCode();

        return webSocketClient.connect()
                .thenCompose(ignored ->
                        webSocketClient.subscribe(
                                approvalKey,
                                stockCode
                        )
                )
                .thenRun(() ->
                        log.info(
                                "KIS 실시간 호가 구독 시작 완료. stockCode={}",
                                stockCode
                        )
                );
    }

    public CompletableFuture<Void> stop() {
        return webSocketClient.disconnect();
    }
}
