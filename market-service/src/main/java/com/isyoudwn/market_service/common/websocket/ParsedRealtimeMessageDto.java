package com.isyoudwn.market_service.common.websocket;

public record ParsedRealtimeMessageDto(
        String encrypted,
        String transactionId,
        String dataCount,
        String body
) {

    public boolean isPlainText() {
        return "0".equals(encrypted);
    }
}
