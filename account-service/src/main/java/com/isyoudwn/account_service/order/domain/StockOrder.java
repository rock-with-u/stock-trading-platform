package com.isyoudwn.account_service.order.domain;

import com.isyoudwn.account_service.account.domain.Account;
import com.isyoudwn.common_service.domain.BaseEntity;
import com.isyoudwn.common_service.stock.domain.Stock;
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
@Table(name = "stock_order")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StockOrder extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_order_root_id")
    private StockOrder rootOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_order_parent_id")
    private StockOrder parentOrder;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_side", nullable = false, length = 10)
    private OrderSide orderSide;

    @Enumerated(EnumType.STRING)
    @Column(name = "price_type", nullable = false, length = 10)
    private OrderPriceType priceType;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_action", nullable = false, length = 10)
    private StockOrderAction orderAction;

    @Column(name = "order_quantity", nullable = false)
    private Long orderQuantity;

    @Column(name = "filled_quantity", nullable = false)
    private Long filledQuantity;

    @Column(name = "remaining_quantity", nullable = false)
    private Long remainingQuantity;

    @Column(name = "canceled_quantity", nullable = false)
    private Long canceledQuantity;

    @Column(name = "limit_price")
    private Long limitPrice;

    @Column(name = "idempotency_key", nullable = false, unique = true, length = 100)
    private String idempotencyKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false, length = 30)
    private StockOrderStatus orderStatus;

    @Column(name = "order_at", nullable = false)
    private LocalDateTime orderedAt;

    private StockOrder(
            Stock stock,
            Account account,
            StockOrder rootOrder,
            StockOrder parentOrder,
            OrderSide orderSide,
            OrderPriceType orderPriceType,
            StockOrderAction orderAction,
            long orderQuantity,
            Long limitPrice,
            String idempotencyKey,
            LocalDateTime orderedAt
    ) {
        this.stock = stock;
        this.account = account;
        this.rootOrder = rootOrder;
        this.parentOrder = parentOrder;
        this.orderSide = orderSide;
        this.priceType = orderPriceType;
        this.orderAction = orderAction;
        this.orderQuantity = orderQuantity;
        this.filledQuantity = 0L;
        this.remainingQuantity = orderQuantity;
        this.canceledQuantity = 0L;
        this.limitPrice = limitPrice;
        this.idempotencyKey = idempotencyKey;
        this.orderStatus = StockOrderStatus.PENDING;
        this.orderedAt = orderedAt;
    }

    public static StockOrder createNew(
            Stock stock,
            Account account,
            OrderSide orderSide,
            OrderPriceType orderPriceType,
            long orderQuantity,
            Long limitPrice,
            String idempotencyKey,
            LocalDateTime orderedAt
    ) {
        return new StockOrder(
                stock,
                account,
                null,
                null,
                orderSide,
                orderPriceType,
                StockOrderAction.NEW,
                orderQuantity,
                limitPrice,
                idempotencyKey,
                orderedAt
        );
    }

    public static StockOrder createAmendment(
            StockOrder parentOrder,
            OrderPriceType orderPriceType,
            long orderQuantity,
            Long limitPrice,
            String idempotencyKey,
            LocalDateTime orderedAt
    ) {
        StockOrder root;

        if (parentOrder.rootOrder == null) {
            root = parentOrder;
        } else {
            root = parentOrder.rootOrder;
        }

        return new StockOrder(
                parentOrder.stock,
                parentOrder.account,
                root,
                parentOrder,
                parentOrder.orderSide,
                orderPriceType,
                StockOrderAction.AMEND,
                orderQuantity,
                limitPrice,
                idempotencyKey,
                orderedAt
        );
    }

    public static StockOrder createCancellation(
            StockOrder parentOrder,
            long orderQuantity,
            String idempotencyKey,
            LocalDateTime orderedAt
    ) {
        StockOrder root;

        if (parentOrder.rootOrder == null) {
            root = parentOrder;
        } else {
            root = parentOrder.rootOrder;
        }

        return new StockOrder(
                parentOrder.stock,
                parentOrder.account,
                root,
                parentOrder,
                parentOrder.orderSide,
                parentOrder.priceType,
                StockOrderAction.CANCEL,
                orderQuantity,
                parentOrder.limitPrice,
                idempotencyKey,
                orderedAt
        );
    }
}
