package com.isyoudwn.market_service.infrastructure.kis.websocket;

import com.isyoudwn.common_service.exception.StockException;
import com.isyoudwn.common_service.response.ResponseMessage;
import com.isyoudwn.common_service.stock.service.StockService;
import com.isyoudwn.market_service.infrastructure.kis.KisApprovalKeyService;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KisQuoteSubscriptionService {

    private final StockService stockService;
    private final KisApprovalKeyService approvalKeyService;
    private final KisQuoteWebSocketClient webSocketClient;

    public CompletableFuture<Void> start() {
        List<String> stockCodes = stockService.getStockCodes();

        if (stockCodes.isEmpty()) {
            return CompletableFuture.failedFuture(new StockException(ResponseMessage.STOCK_NOT_FOUND)
            );
        }

        String approvalKey = approvalKeyService.getOrIssue();

        return webSocketClient
                .connect()
                .thenCompose(ignored -> subscribeAll(approvalKey, stockCodes))
                .thenRun(() ->
                        log.info("KIS 실시간 호가 구독 시작 완료. stockCount={}", stockCodes.size())
                );
    }

    private CompletableFuture<Void> subscribeAll(
            String approvalKey,
            List<String> stockCodes
    ) {
        List<CompletableFuture<Void>> futures = stockCodes.stream()
                .map(stockCode -> webSocketClient.subscribe(approvalKey, stockCode))
                .toList();

        return CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0])
        );
    }

    public CompletableFuture<Void> stop() {
        return webSocketClient.disconnect();
    }
}
