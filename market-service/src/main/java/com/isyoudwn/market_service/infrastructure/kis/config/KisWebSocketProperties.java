package com.isyoudwn.market_service.infrastructure.kis.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kis.websocket")
public record KisWebSocketProperties(
        String webSocketUrl
) {}
