package com.isyoudwn.market_service.common.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KisRealtimeMessageParser {

    public ParsedRealtimeMessageDto parse(String message) {
        String[] parts = message.split("\\|", 4);

        if (parts.length < 4) {
            return null;
        }

        return new ParsedRealtimeMessageDto(
                parts[0],
                parts[1],
                parts[2],
                parts[3]
        );
    }
}
