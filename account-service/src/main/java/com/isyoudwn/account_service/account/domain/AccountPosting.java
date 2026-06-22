package com.isyoudwn.account_service.account.domain;

import com.isyoudwn.common_service.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "account_posting")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountPosting extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_posting_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(name = "posting_type", nullable = false, length = 50)
    private PostingType postingType;

    @Column(name = "source_id", nullable = false)
    private Long sourceId;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false, length = 50)
    private PostingSourceType sourceType;

    @Column(name = "idempotency_key", nullable = false, unique = true, length = 100)
    private String idempotencyKey;

    @Column(name = "occurred_at", nullable = false)
    private LocalDateTime occurredAt;

    private AccountPosting(
            Account account,
            PostingType postingType,
            Long sourceId,
            PostingSourceType sourceType,
            String idempotencyKey,
            LocalDateTime occurredAt
    ) {
        this.account = account;
        this.postingType = postingType;
        this.sourceId = sourceId;
        this.sourceType = sourceType;
        this.idempotencyKey = idempotencyKey;
        this.occurredAt = occurredAt;
    }

    public static AccountPosting create(
            Account account,
            PostingType postingType,
            Long sourceId,
            PostingSourceType sourceType,
            String idempotencyKey,
            LocalDateTime occurredAt
    ) {
        return new AccountPosting(account, postingType, sourceId, sourceType, idempotencyKey, occurredAt);
    }
}

