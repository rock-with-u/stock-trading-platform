package com.isyoudwn.account_service.order.infrastructure.repository;

import com.isyoudwn.account_service.order.infrastructure.outbox.OrderOutboxEvent;
import com.isyoudwn.account_service.order.infrastructure.outbox.OrderOutboxEventStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderOutboxEventRepository extends JpaRepository<OrderOutboxEvent, Long> {

    @Query("""
            select e
            from OrderOutboxEvent e
            where e.status in :statuses
              and e.nextRetryAt <= :now
            order by e.id asc
            """)
    List<OrderOutboxEvent> findPublishableEvents(
            @Param("statuses") List<OrderOutboxEventStatus> statuses,
            @Param("now") LocalDateTime now,
            Pageable pageable
    );

    Optional<OrderOutboxEvent> findByEventId(String eventId);
}
