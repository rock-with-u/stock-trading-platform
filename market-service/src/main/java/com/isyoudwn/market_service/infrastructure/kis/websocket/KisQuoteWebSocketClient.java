package com.isyoudwn.market_service.infrastructure.kis.websocket;

import com.isyoudwn.common_service.config.TimeProvider;
import com.isyoudwn.market_service.infrastructure.kis.config.KisWebSocketProperties;
import com.isyoudwn.market_service.infrastructure.kis.dto.KisQuoteWebSocketDto;
import com.isyoudwn.market_service.infrastructure.kis.kafka.event.QuoteSnapshotEvent;
import com.isyoudwn.market_service.infrastructure.kis.kafka.event.QuoteSnapshotEvent.QuoteLevel;
import com.isyoudwn.market_service.infrastructure.kis.kafka.producer.QuoteEventProducer;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class KisQuoteWebSocketClient {

    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(10);
    private static final String QUOTE_TRANSACTION_ID = "H0STASP0";

    private final ObjectMapper objectMapper;
    private final QuoteEventProducer quoteEventProducer;
    private final KisWebSocketProperties kisWebSocketProperties;
    private final TimeProvider timeProvider;

    private WebSocket webSocket;

    public CompletableFuture<Void> connect() {
        if (isConnected()) {
            return CompletableFuture.completedFuture(null);
        }

        URI websocketUri = URI.create(kisWebSocketProperties.webSocketUrl());
        log.info("KIS 웹소켓 연결 시작. uri={}", websocketUri);

        return HttpClient
                .newHttpClient()
                .newWebSocketBuilder()
                .connectTimeout(CONNECT_TIMEOUT)
                .buildAsync(websocketUri, new KisWebSocketListener())
                .thenAccept(connectedWebSocket -> {
                    this.webSocket = connectedWebSocket;
                    log.info("KIS 웹소켓 연결 완료");
                });
    }

    public CompletableFuture<Void> subscribe(
            String approvalKey,
            String stockCode
    ) {
        KisQuoteWebSocketDto.Request request = KisQuoteWebSocketDto.Request.subscribe(approvalKey, stockCode);

        return send(request)
                .thenRun(() -> log.info("KIS 호가 구독 요청 완료. stockCode={}", stockCode));
    }

    public CompletableFuture<Void> unsubscribe(
            String approvalKey,
            String stockCode
    ) {
        KisQuoteWebSocketDto.Request request = KisQuoteWebSocketDto.Request.unsubscribe(approvalKey, stockCode);

        return send(request)
                .thenRun(() -> log.info("KIS 호가 구독 해제 요청 완료. stockCode={}", stockCode));
    }

    private CompletableFuture<Void> send(
            KisQuoteWebSocketDto.Request request
    ) {
        if (!isConnected()) {
            return CompletableFuture
                    .failedFuture(new IllegalStateException("KIS 웹소켓이 연결되어 있지 않습니다."));
        }

        try {
            String payload = objectMapper.writeValueAsString(request);

            return webSocket
                    .sendText(payload, true)
                    .thenAccept(ignored -> log.debug("KIS 웹소켓 메시지 전송: {}", payload));

        } catch (Exception exception) {
            return CompletableFuture.failedFuture(exception);
        }
    }

    public CompletableFuture<Void> disconnect() {
        if (webSocket == null) {
            return CompletableFuture.completedFuture(null);
        }

        WebSocket currentWebSocket = webSocket;
        webSocket = null;

        return currentWebSocket
                .sendClose(WebSocket.NORMAL_CLOSURE, "Application shutdown")
                .thenAccept(ignored -> log.info("KIS 웹소켓 연결 종료")
                );
    }

    public boolean isConnected() {
        return webSocket != null && !webSocket.isInputClosed() && !webSocket.isOutputClosed();
    }

    private void handleMessage(String message) {
        if (message == null || message.isBlank()) {
            return;
        }

        if (message.startsWith("{")) {
            log.info("KIS 웹소켓 응답 수신: {}", message);
            return;
        }

        String[] messageParts = message.split("\\|", 4);

        if (messageParts.length < 4) {
            log.warn("알 수 없는 KIS 웹소켓 메시지: {}", message);
            return;
        }

        String transactionId = messageParts[1];
        String rawData = messageParts[3];

        if (!QUOTE_TRANSACTION_ID.equals(transactionId)) {
            log.debug("처리하지 않는 KIS 메시지입니다. trId={}", transactionId);
            return;
        }

        String[] fields = rawData.split("\\^");

        if (fields.length == 0) {
            return;
        }

        String stockCode = fields[0];

        QuoteSnapshotEvent event = parseQuoteSnapshot(rawData);

        quoteEventProducer.publish(event);

        log.info(
                "KIS 실시간 호가 수신. stockCode={}, fieldCount={}",
                stockCode,
                fields.length
        );
    }

    private class KisWebSocketListener implements WebSocket.Listener {

        private final StringBuilder messageBuffer = new StringBuilder();

        @Override
        public void onOpen(WebSocket webSocket) {
            log.info("KIS 웹소켓 세션 열림");
            webSocket.request(1);
        }

        @Override
        public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
            messageBuffer.append(data);

            if (last) {
                String message = messageBuffer.toString();
                messageBuffer.setLength(0);

                try {
                    handleMessage(message);
                } catch (RuntimeException exception) {
                    log.error("KIS 웹소켓 메시지 처리 실패. message={}", message, exception);
                }
            }

            webSocket.request(1);
            return CompletableFuture.completedFuture(null);
        }

        @Override
        public CompletionStage<?> onClose(WebSocket closedWebSocket, int statusCode, String reason) {
            webSocket = null;

            log.warn(
                    "KIS 웹소켓 연결 종료. statusCode={}, reason={}",
                    statusCode,
                    reason
            );

            return CompletableFuture.completedFuture(null);
        }

        @Override
        public void onError(WebSocket errorWebSocket, Throwable error) {
            webSocket = null;
            log.error("KIS 웹소켓 오류 발생", error);
        }
    }

    private QuoteSnapshotEvent parseQuoteSnapshot(String rawData) {
        String[] fields = rawData.split("\\^");

        if (fields.length < 43) {
            throw new IllegalArgumentException("KIS 호가 필드 수가 부족합니다. fieldCount=" + fields.length);
        }

        String stockCode = fields[0];

        List<QuoteLevel> asks = new ArrayList<>();
        List<QuoteSnapshotEvent.QuoteLevel> bids = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            int level = i + 1;

            long askPrice = parseLong(fields[3 + i]);
            long bidPrice = parseLong(fields[13 + i]);

            long askQuantity = parseLong(fields[23 + i]);
            long bidQuantity = parseLong(fields[33 + i]);

            asks.add(new QuoteSnapshotEvent.QuoteLevel(level, askPrice, askQuantity));

            bids.add(new QuoteSnapshotEvent.QuoteLevel(
                    level,
                    bidPrice,
                    bidQuantity
            ));
        }

        return new QuoteSnapshotEvent(
                stockCode,
                timeProvider.now(),
                asks,
                bids
        );
    }

    private long parseLong(String value) {
        if (value == null || value.isBlank()) {
            return 0L;
        }

        return Long.parseLong(value.trim());
    }
}
