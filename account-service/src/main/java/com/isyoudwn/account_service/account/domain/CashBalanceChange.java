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
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
        name = "cash_balance_change",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_cash_balance_change_posting_bucket",
                columnNames = {"account_posting_id", "cash_bucket"}
        )
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CashBalanceChange extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cash_balance_change_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_posting_id", nullable = false)
    private AccountPosting accountPosting;

    @Enumerated(EnumType.STRING)
    @Column(name = "cash_bucket", nullable = false, length = 50)
    private CashBucket cashBucket;

    @Column(name = "amount_before", nullable = false)
    private Long amountBefore;

    @Column(name = "amount_after", nullable = false)
    private Long amountAfter;

    @Column(name = "delta_amount", nullable = false)
    private Long deltaAmount;

    private CashBalanceChange(
            AccountPosting accountPosting,
            CashBucket cashBucket,
            long amountBefore,
            long amountAfter
    ) {
        this.accountPosting = accountPosting;
        this.cashBucket = cashBucket;
        this.amountBefore = amountBefore;
        this.amountAfter = amountAfter;
        this.deltaAmount = amountAfter - amountBefore;
    }

    public static CashBalanceChange create(
            AccountPosting accountPosting,
            CashBucket cashBucket,
            long amountBefore,
            long amountAfter
    ) {
        return new CashBalanceChange(accountPosting, cashBucket, amountBefore, amountAfter);
    }
}
