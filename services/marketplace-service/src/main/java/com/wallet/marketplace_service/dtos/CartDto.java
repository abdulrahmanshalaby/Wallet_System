package com.wallet.marketplace_service.dtos;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartDto {
    private Long buyerId;
    private List<CartItemDto> items;
}
