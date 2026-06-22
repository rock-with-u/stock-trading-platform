package com.isyoudwn.market_service.common.websocket;

import com.isyoudwn.market_service.common.config.KisWebSocketProperties;
import com.isyoudwn.market_service.common.websocket.KisWebSocketRequestDto.Request;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class KisRealtimeWebSocketClient {

    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(10);

    private final ObjectMapper objectMapper;
    private final KisWebSocketProperties kisWebSocketProperties;
    private final KisRealtimeWebSocketMessageDispatcher messageDispatcher;

    private WebSocket webSocket;

    public CompletableFuture<Void> connect() {
        if (isConnected()) {
            return CompletableFuture.completedFuture(null);
        }

        URI websocketUri = URI.create(kisWebSocketProperties.webSocketUrl());
        log.info("KIS 실시간 웹소켓 연결 시작. uri={}", websocketUri);

        return HttpClient
                .newHttpClient()
                .newWebSocketBuilder()
                .connectTimeout(CONNECT_TIMEOUT)
                .buildAsync(websocketUri, new KisWebSocketListener())
                .thenAccept(connectedWebSocket -> {
                    this.webSocket = connectedWebSocket;
                    log.info("KIS 실시간 웹소켓 연결 완료");
                });
    }

    public CompletableFuture<Void> send(
            Request request
    ) {
        if (!isConnected()) {
            return CompletableFuture.failedFuture(
                    new IllegalStateException("KIS 실시간 웹소켓이 연결되어 있지 않습니다.")
            );
        }

        try {
            String payload = objectMapper.writeValueAsString(request);

            return webSocket
                    .sendText(payload, true)
                    .thenAccept(ignored -> log.debug("KIS 실시간 웹소켓 메시지 전송: {}", payload));

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
                .thenAccept(ignored -> log.info("KIS 실시간 웹소켓 연결 종료"));
    }

    public boolean isConnected() {
        return webSocket != null
                && !webSocket.isInputClosed()
                && !webSocket.isOutputClosed();
    }

    private class KisWebSocketListener implements WebSocket.Listener {

        private final StringBuilder messageBuffer = new StringBuilder();

        @Override
        public void onOpen(WebSocket webSocket) {
            log.info("KIS 실시간 웹소켓 세션 열림");
            webSocket.request(1);
        }

        @Override
        public CompletionStage<?> onText(
                WebSocket webSocket,
                CharSequence data,
                boolean last
        ) {
            messageBuffer.append(data);

            if (last) {
                String message = messageBuffer.toString();
                messageBuffer.setLength(0);

                try {
                    messageDispatcher.dispatch(message);
                } catch (RuntimeException exception) {
                    log.error("KIS 실시간 웹소켓 메시지 처리 실패. message={}", message, exception);
                }
            }

            webSocket.request(1);
            return CompletableFuture.completedFuture(null);
        }

        @Override
        public CompletionStage<?> onClose(
                WebSocket closedWebSocket,
                int statusCode,
                String reason
        ) {
            webSocket = null;

            log.warn(
                    "KIS 실시간 웹소켓 연결 종료. statusCode={}, reason={}",
                    statusCode,
                    reason
            );

            return CompletableFuture.completedFuture(null);
        }

        @Override
        public void onError(
                WebSocket errorWebSocket,
                Throwable error
        ) {
            webSocket = null;
            log.error("KIS 실시간 웹소켓 오류 발생", error);
        }
    }
}
