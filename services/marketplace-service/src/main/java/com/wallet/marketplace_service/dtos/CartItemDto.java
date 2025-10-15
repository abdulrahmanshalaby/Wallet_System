package com.wallet.marketplace_service.dtos;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemDto {
    private Long productId;
    private int quantity;
}
