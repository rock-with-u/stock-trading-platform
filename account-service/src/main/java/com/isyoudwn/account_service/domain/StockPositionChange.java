package com.isyoudwn.account_service.domain;

import com.isyoudwn.common_service.domain.BaseEntity;
import com.isyoudwn.common_service.domain.Stock;
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
        name = "stock_position_change",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_stock_position_change_posting_stock_bucket",
                columnNames = {"account_posting_id", "stock_id", "position_bucket"}
        )
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StockPositionChange extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_position_change_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_posting_id", nullable = false)
    private AccountPosting accountPosting;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;

    @Enumerated(EnumType.STRING)
    @Column(name = "position_bucket", nullable = false, length = 50)
    private PositionBucket positionBucket;

    @Column(name = "quantity_before", nullable = false)
    private Long quantityBefore;

    @Column(name = "delta_quantity", nullable = false)
    private Long deltaQuantity;

    @Column(name = "quantity_after", nullable = false)
    private Long quantityAfter;

    @Column(name = "average_price_before", nullable = false)
    private Long averagePriceBefore;

    @Column(name = "average_price_after", nullable = false)
    private Long averagePriceAfter;

    private StockPositionChange(
            AccountPosting accountPosting,
            Stock stock,
            PositionBucket positionBucket,
            long quantityBefore,
            long quantityAfter,
            long averagePriceBefore,
            long averagePriceAfter
    ) {
        this.accountPosting = accountPosting;
        this.stock = stock;
        this.positionBucket = positionBucket;
        this.quantityBefore = quantityBefore;
        this.deltaQuantity = quantityAfter - quantityBefore;
        this.quantityAfter = quantityAfter;
        this.averagePriceBefore = averagePriceBefore;
        this.averagePriceAfter = averagePriceAfter;
    }

    public static StockPositionChange create(
            AccountPosting accountPosting,
            Stock stock,
            PositionBucket positionBucket,
            long quantityBefore,
            long quantityAfter,
            long averagePriceBefore,
            long averagePriceAfter
    ) {
        return new StockPositionChange(
                accountPosting,
                stock,
                positionBucket,
                quantityBefore,
                quantityAfter,
                averagePriceBefore,
                averagePriceAfter
        );
    }
}
