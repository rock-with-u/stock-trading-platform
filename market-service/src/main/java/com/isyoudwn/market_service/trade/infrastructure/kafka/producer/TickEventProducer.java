package com.isyoudwn.market_service.trade.infrastructure.kafka.producer;

import com.isyoudwn.market_service.trade.infrastructure.kafka.event.TickSnapshotEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class TickEventProducer {

    private static final String TOPIC = "market.trade";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publish(TickSnapshotEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);

            kafkaTemplate.send(TOPIC, event.stockCode(), payload)
                    .whenComplete((result, exception) -> {
                        if (exception != null) {
                            log.error("Kafka 체결 이벤트 발행 실패. stockCode={}", event.stockCode(), exception);
                            return;
                        }

                        log.info(
                                "Kafka 체결 이벤트 발행 성공. topic={}, partition={}, offset={}, key={}",
                                result.getRecordMetadata().topic(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset(),
                                event.stockCode()
                        );
                    });

        } catch (Exception exception) {
            throw new IllegalStateException("체결 이벤트 Kafka 발행 실패", exception);
        }
    }
}
