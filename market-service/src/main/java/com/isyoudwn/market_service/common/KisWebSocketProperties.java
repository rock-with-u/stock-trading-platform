package com.isyoudwn.market_service.common;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kis.websocket")
public record KisWebSocketProperties(
        String webSocketUrl
) {}
