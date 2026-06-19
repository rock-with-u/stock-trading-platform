package com.isyoudwn.market_service.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

@Configuration
public class KisWebSocketConfig {

    @Bean
    public WebSocketClient kisWebSocketClient() {
        return new StandardWebSocketClient();
    }
}
