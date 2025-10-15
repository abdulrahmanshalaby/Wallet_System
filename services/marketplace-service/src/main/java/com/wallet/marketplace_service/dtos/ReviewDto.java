package com.wallet.marketplace_service.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDto {
    private Long orderItemId;    
    private Long buyerId;
    private int rating;
    private String comment;
    
}
