package com.wallet.marketplace_service.dtos;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDto {
    private Long id;
    private Long buyerId;
    private String buyerName;
    private String buyerEmail;
    private String buyerPhone;
    private String status;
    private BigDecimal totalAmount;
    private double deliveryLatitude;
    private double deliveryLongitude;
    private List<OrderItemDto> items;
}