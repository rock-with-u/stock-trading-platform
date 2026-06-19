package com.isyoudwn.market_service.infrastructure.kis.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kis")
public record KisProperties(
        String appKey,
        String appSecret,
        String restBaseUrl
) {
}
