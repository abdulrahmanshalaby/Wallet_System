package com.wallet.marketplace_service.dtos;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryDto {
    private Long productId;
    private int availableQuantity;
    private int reservedQuantity;
}
