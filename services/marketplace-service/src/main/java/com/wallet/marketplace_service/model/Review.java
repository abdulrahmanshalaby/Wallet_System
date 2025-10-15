package com.wallet.marketplace_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id")
    private OrderItem orderItem;

    private Long buyerId;

    private int rating; // 1–5 stars

    @Column(length = 2000)
    private String comment;

    private final  LocalDateTime createdAt = LocalDateTime.now();
}
