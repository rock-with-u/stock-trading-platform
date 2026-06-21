package com.isyoudwn.account_service.order.presentaion;

import com.isyoudwn.account_service.order.application.CreateStockOrderService;
import com.isyoudwn.account_service.order.presentaion.dto.OrderRequestDto;
import com.isyoudwn.common_service.response.ApiResponse;
import com.isyoudwn.common_service.response.ResponseMessage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/orders")
public class StockOrderController {

    private final CreateStockOrderService createStockOrderService;

    @PostMapping("")
    public ResponseEntity<ApiResponse<Void>> createOrder(
            @Valid
            @RequestBody
            OrderRequestDto.CreateOrderDto createOrderDto
    ) {

        createStockOrderService.createNewOrder(createOrderDto);

        return ResponseEntity
                .ok()
                .body(ApiResponse.succeed(ResponseMessage.SUCCESS.getCode(), ResponseMessage.SUCCESS.getMessage(),
                        null));
    }
}
