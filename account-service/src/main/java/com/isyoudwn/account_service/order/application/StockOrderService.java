package com.isyoudwn.account_service.order.application;

import com.isyoudwn.account_service.order.presentaion.dto.OrderRequestDto;

public interface StockOrderService {
    void createNewOrder(OrderRequestDto.CreateOrderDto createOrderDto);
}
