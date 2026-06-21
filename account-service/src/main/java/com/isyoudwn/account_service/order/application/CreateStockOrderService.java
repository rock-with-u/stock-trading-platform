package com.isyoudwn.account_service.order.application;

import com.isyoudwn.account_service.order.presentaion.dto.OrderRequestDto;

public interface CreateStockOrderService {
    void createNewOrder(OrderRequestDto.CreateOrderDto createOrderDto);
}
