package com.wallet.marketplace_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long buyerId;
    private String buyerName;
    private String buyerEmail;
    private String buyerPhone;

    private String status; // PENDING_PAYMENT, PAID, SHIPPED, DELIVERED, CANCELLED

    private BigDecimal totalAmount;

    private final LocalDateTime createdAt = LocalDateTime.now();

    private double deliveryLatitude;
    private double deliveryLongitude;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items;
}
