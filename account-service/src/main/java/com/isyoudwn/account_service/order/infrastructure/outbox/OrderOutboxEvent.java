package com.isyoudwn.account_service.order.infrastructure.outbox;

import com.isyoudwn.common_service.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "order_outbox_event")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderOutboxEvent extends BaseEntity {

    private static final int MAX_RETRY_COUNT = 5;
    private static final int MAX_FAILURE_REASON_LENGTH = 500;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_outbox_event_id")
    private Long id;

    @Column(name = "event_id", nullable = false, unique = true, updatable = false)
    private String eventId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private OrderOutboxEventType eventType;

    @Column(name = "stock_order_id", nullable = false)
    private Long stockOrderId;

    @Lob
    @Column(name = "payload", nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderOutboxEventStatus status;

    @Column(name = "retry_count", nullable = false)
    private int retryCount;

    @Column(name = "next_retry_at")
    private LocalDateTime nextRetryAt;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "failure_reason", length = 500)
    private String failureReason;

    private OrderOutboxEvent(
            String eventId,
            OrderOutboxEventType eventType,
            Long stockOrderId,
            String payload,
            LocalDateTime now
    ) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.stockOrderId = stockOrderId;
        this.payload = payload;
        this.status = OrderOutboxEventStatus.PENDING;
        this.retryCount = 0;
        this.nextRetryAt = now;
    }

    public static OrderOutboxEvent create(
            String eventId,
            OrderOutboxEventType eventType,
            Long stockOrderId,
            String payload,
            LocalDateTime now
    ) {
        return new OrderOutboxEvent(eventId, eventType, stockOrderId, payload, now);
    }

    public void markPublished(LocalDateTime now) {
        this.status = OrderOutboxEventStatus.PUBLISHED;
        this.publishedAt = now;
        this.nextRetryAt = null;
        this.failureReason = null;
    }

    public void markFailed(String reason, LocalDateTime now) {
        this.retryCount += 1;
        this.failureReason = truncateFailureReason(reason);

        if (this.retryCount >= MAX_RETRY_COUNT) {
            this.status = OrderOutboxEventStatus.DEAD_LETTERED;
            this.nextRetryAt = null;
            return;
        }

        this.status = OrderOutboxEventStatus.FAILED;
        this.nextRetryAt = now.plusSeconds(calculateBackoffSeconds());
    }

    private long calculateBackoffSeconds() {
        if (retryCount == 1) {
            return 1;
        }
        if (retryCount == 2) {
            return 3;
        }
        if (retryCount == 3) {
            return 10;
        }
        if (retryCount == 4) {
            return 30;
        }
        return 60;
    }

    private String truncateFailureReason(String reason) {
        if (reason == null || reason.isBlank()) {
            return "Unknown failure";
        }

        if (reason.length() <= MAX_FAILURE_REASON_LENGTH) {
            return reason;
        }

        return reason.substring(0, MAX_FAILURE_REASON_LENGTH);
    }
}
