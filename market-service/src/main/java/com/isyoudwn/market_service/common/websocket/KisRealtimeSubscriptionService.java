package com.isyoudwn.market_service.common.websocket;

import com.isyoudwn.common_service.exception.StockException;
import com.isyoudwn.common_service.response.ResponseMessage;
import com.isyoudwn.common_service.stock.service.StockService;
import com.isyoudwn.market_service.auth.application.KisApprovalKeyService;
import com.isyoudwn.market_service.quote.infrastructure.dto.KisQuoteWebSocketRequestDto;
import com.isyoudwn.market_service.trade.infrastructure.dto.KisTradeWebSocketRequestDto;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KisRealtimeSubscriptionService {

    private final StockService stockService;
    private final KisApprovalKeyService approvalKeyService;
    private final KisRealtimeWebSocketClient realtimeWebSocketClient;

    public CompletableFuture<Void> start() {
        List<String> stockCodes = stockService.getStockCodes();

        if (stockCodes.isEmpty()) {
            return CompletableFuture.failedFuture(
                    new StockException(ResponseMessage.STOCK_NOT_FOUND)
            );
        }

        String approvalKey = approvalKeyService.getOrIssue();

        return realtimeWebSocketClient
                .connect()
                .thenCompose(ignored -> subscribeAll(approvalKey, stockCodes))
                .thenRun(() -> log.info(
                        "KIS 실시간 호가 및 체결가 구독 시작 완료. stockCount={}",
                        stockCodes.size()
                ));
    }

    public CompletableFuture<Void> stop() {
        return realtimeWebSocketClient
                .disconnect()
                .thenRun(() -> log.info("KIS 실시간 호가/체결가 구독 종료 완료"));
    }

    private CompletableFuture<Void> subscribeAll(
            String approvalKey,
            List<String> stockCodes
    ) {
        CompletableFuture<Void> chain = CompletableFuture.completedFuture(null);

        for (String stockCode : stockCodes) {
            chain = chain
                    .thenCompose(ignored -> subscribeQuote(approvalKey, stockCode))
                    .thenCompose(ignored -> subscribeTrade(approvalKey, stockCode));
        }

        return chain;
    }

    private CompletableFuture<Void> subscribeQuote(
            String approvalKey,
            String stockCode
    ) {
        return realtimeWebSocketClient
                .send(KisQuoteWebSocketRequestDto.subscribe(approvalKey, stockCode))
                .thenRun(() -> log.info("KIS 호가 구독 요청 완료. stockCode={}", stockCode));
    }

    private CompletableFuture<Void> subscribeTrade(
            String approvalKey,
            String stockCode
    ) {
        return realtimeWebSocketClient
                .send(KisTradeWebSocketRequestDto.subscribe(approvalKey, stockCode))
                .thenRun(() -> log.info("KIS 체결가 구독 요청 완료. stockCode={}", stockCode));
    }
}
