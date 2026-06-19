package com.isyoudwn.account_service.domain.account;

import com.isyoudwn.common_service.domain.BaseEntity;
import com.isyoudwn.common_service.stock.domain.Stock;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
        name = "stock_position",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_stock_position_account_stock",
                columnNames = {"account_id", "stock_id"}
        )
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StockPosition extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_position_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(name = "average_price", nullable = false)
    private Long averagePrice;

    @Column(name = "position_quantity", nullable = false)
    private Long positionQuantity;

    @Column(name = "sell_reserved_quantity", nullable = false)
    private Long sellReservedQuantity;

    private StockPosition(Account account, Stock stock) {
        this.account = account;
        this.stock = stock;
        this.averagePrice = 0L;
        this.positionQuantity = 0L;
        this.sellReservedQuantity = 0L;
    }

    public static StockPosition create(Account account, Stock stock) {
        return new StockPosition(account, stock);
    }
}
