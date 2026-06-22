package com.isyoudwn.account_service.order.presentaion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class OrderRequestDto {

    public record CreateOrderDto(

            @NotBlank(message = "가격 유형은 필수입니다.")
            String priceType,

            @NotBlank(message = "종목 코드는 필수입니다.")
            String stockCode,

            @NotBlank(message = "주문 방향은 필수입니다.")
            String orderSide,

            @NotNull(message = "주문 수량은 필수입니다.")
            Long orderQuantity,

            Long limitPrice,

            @NotBlank(message = "계좌 번호는 필수입니다.")
            String accountNumber,

            @NotBlank(message = "멱등성 키는 필수입니다.")
            String idempotencyKey
    ) {
    }
}
