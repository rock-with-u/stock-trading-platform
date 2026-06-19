package com.isyoudwn.market_service.infrastructure.kis.kafka.producer;

import com.isyoudwn.market_service.infrastructure.kis.kafka.event.QuoteSnapshotEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class QuoteEventProducer {

    private static final String TOPIC = "market.quote";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publish(QuoteSnapshotEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);

            kafkaTemplate.send(TOPIC, event.stockCode(), payload)
                    .whenComplete((result, exception) -> {
                        if (exception != null) {
                            log.error("Kafka 호가 발행 실패. stockCode={}", event.stockCode(), exception);
                            return;
                        }

                        log.info(
                                "Kafka 호가 발행 성공. topic={}, partition={}, offset={}, key={}",
                                result.getRecordMetadata().topic(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset(),
                                event.stockCode()
                        );
                    });

        } catch (Exception exception) {
            throw new IllegalStateException("호가 이벤트 Kafka 발행 실패", exception);
        }
    }
}
