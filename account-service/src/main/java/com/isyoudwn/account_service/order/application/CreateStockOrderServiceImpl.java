package com.isyoudwn.account_service.order.application;

import com.isyoudwn.account_service.account.application.AccountService;
import com.isyoudwn.account_service.account.application.CashBalanceChangeService;
import com.isyoudwn.account_service.account.application.StockPositionChangeService;
import com.isyoudwn.account_service.account.application.StockPositionService;
import com.isyoudwn.account_service.account.domain.Account;
import com.isyoudwn.account_service.account.domain.AccountPosting;
import com.isyoudwn.account_service.account.domain.PostingSourceType;
import com.isyoudwn.account_service.account.domain.PostingType;
import com.isyoudwn.account_service.account.domain.StockPosition;
import com.isyoudwn.account_service.account.domain.dto.BuyReservationResult;
import com.isyoudwn.account_service.account.domain.dto.SellReservationResult;
import com.isyoudwn.account_service.account.infrastructure.AccountPostingRepository;
import com.isyoudwn.account_service.order.domain.OrderPriceType;
import com.isyoudwn.account_service.order.domain.OrderSide;
import com.isyoudwn.account_service.order.domain.StockOrder;
import com.isyoudwn.account_service.order.infrastructure.repository.StockOrderRepository;
import com.isyoudwn.account_service.order.presentaion.dto.OrderRequestDto.CreateOrderDto;
import com.isyoudwn.account_service.order.infrastructure.outbox.OrderOutboxPayloadSerializer;
import com.isyoudwn.account_service.order.infrastructure.outbox.OrderOutboxEvent;
import com.isyoudwn.account_service.order.infrastructure.outbox.OrderOutboxEventType;
import com.isyoudwn.account_service.order.infrastructure.outbox.message.LimitOrderCreatedMessage;
import com.isyoudwn.account_service.order.infrastructure.repository.OrderOutboxEventRepository;
import com.isyoudwn.common_service.config.TimeProvider;
import com.isyoudwn.common_service.stock.domain.Stock;
import com.isyoudwn.common_service.stock.service.StockService;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateStockOrderServiceImpl implements CreateStockOrderService {

    private final StockService stockService;
    private final AccountService accountService;
    private final CashBalanceChangeService cashBalanceChangeService;
    private final StockPositionService stockPositionService;
    private final StockPositionChangeService stockPositionChangeService;
    private final OrderOutboxPayloadSerializer orderOutboxPayloadSerializer;

    private final StockOrderRepository stockOrderRepository;
    private final AccountPostingRepository accountPostingRepository;
    private final OrderOutboxEventRepository orderOutboxEventRepository;

    private final TimeProvider timeProvider;

    @Override
    @Transactional
    public void createNewOrder(CreateOrderDto createOrderDto) {
        Stock stock = stockService.getByStockCode(createOrderDto.stockCode());
        Account account = accountService.getByAccountNumber(createOrderDto.accountNumber());
        OrderSide orderSide = OrderSide.of(createOrderDto.orderSide());
        OrderPriceType orderPriceType = OrderPriceType.of(createOrderDto.priceType());
        LocalDateTime orderedAt = timeProvider.now();

        StockOrder stockOrder = StockOrder.createNew(
                stock,
                account,
                orderSide,
                orderPriceType,
                createOrderDto.orderQuantity(),
                createOrderDto.limitPrice(),
                createOrderDto.idempotencyKey(),
                orderedAt
        );

        if (OrderPriceType.LIMIT == orderPriceType) {
            if (OrderSide.BUY == orderSide) {
                buyLimitPrice(stockOrder, account, createOrderDto, orderedAt);
            } else {
                sellLimitPrice(stockOrder, account, createOrderDto, stock, orderedAt);
            }
        } else {
            // 시장가
        }
    }

    private void buyLimitPrice(StockOrder stockOrder, Account account, CreateOrderDto createOrderDto,
                               LocalDateTime orderedAt) {
        long orderAmount = createOrderDto.limitPrice() * createOrderDto.orderQuantity();
        long settledBefore = account.getAvailableSettledCashAmount();
        long unsettledBefore = account.getUnsettledSellReceivableAmount();
        long reservedBefore = account.getReservedBuyAmount();

        BuyReservationResult reservationResult = account.reserveBuyAmount(orderAmount);
        StockOrder savedOrder = stockOrderRepository.save(stockOrder);

        AccountPosting posting = accountPostingRepository.save(
                AccountPosting.create(
                        account,
                        PostingType.BUY_ORDER_RESERVED,
                        savedOrder.getId(),
                        PostingSourceType.STOCK_ORDER,
                        createOrderDto.idempotencyKey(),
                        orderedAt
                )
        );

        cashBalanceChangeService.writeBuyReservation(
                posting,
                settledBefore,
                unsettledBefore,
                reservedBefore,
                reservationResult
        );

        saveLimitOrderCreatedOutboxEvent(savedOrder, createOrderDto, orderedAt);
    }

    private void sellLimitPrice(StockOrder stockOrder, Account account, CreateOrderDto createOrderDto,
                                Stock stock, LocalDateTime orderedAt) {
        StockPosition stockPosition = stockPositionService.getByAccount(account, stock);

        SellReservationResult sellReservationResult = stockPosition.reserveSell(createOrderDto.orderQuantity());

        StockOrder savedOrder = stockOrderRepository.save(stockOrder);

        AccountPosting posting = accountPostingRepository.save(
                AccountPosting.create(
                        account,
                        PostingType.SELL_QUANTITY_RESERVED,
                        savedOrder.getId(),
                        PostingSourceType.STOCK_ORDER,
                        createOrderDto.idempotencyKey(),
                        orderedAt
                )
        );

        stockPositionChangeService.writeSellReservation(
                posting,
                stock,
                stockPosition,
                sellReservationResult
        );

        saveLimitOrderCreatedOutboxEvent(savedOrder, createOrderDto, orderedAt);
    }

    private void saveLimitOrderCreatedOutboxEvent(StockOrder savedOrder, CreateOrderDto createOrderDto, LocalDateTime orderedAt) {
        String eventId = UUID.randomUUID().toString();

        LimitOrderCreatedMessage message = new LimitOrderCreatedMessage(
                eventId,
                savedOrder.getId(),
                createOrderDto.accountNumber(),
                createOrderDto.stockCode(),
                createOrderDto.orderSide(),
                createOrderDto.orderQuantity(),
                createOrderDto.limitPrice(),
                orderedAt,
                createOrderDto.idempotencyKey()
        );

        String payload = orderOutboxPayloadSerializer.serialize(message);

        OrderOutboxEvent outboxEvent = OrderOutboxEvent.create(
                eventId,
                OrderOutboxEventType.LIMIT_ORDER_CREATED,
                savedOrder.getId(),
                payload,
                orderedAt
        );

        orderOutboxEventRepository.save(outboxEvent);
    }
}
