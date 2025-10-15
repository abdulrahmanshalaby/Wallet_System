package com.wallet.marketplace_service.dtos;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {
    private Long id;
    private Long sellerId;
    private String title;
    private String description;
    private BigDecimal price;
    private Long categoryId;
    private String locationCity;
    private String locationCountry;
    private List<String> images;
}
