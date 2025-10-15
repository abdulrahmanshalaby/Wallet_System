package com.wallet.marketplace_service.dtos;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class CheckoutRequestDto {
    private String buyerName;
    private String buyerEmail;
    private String buyerPhone;
    private double deliveryLatitude;
    private double deliveryLongitude;
    
}
