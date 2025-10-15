package com.wallet.marketplace_service.Repository;

import com.wallet.marketplace_service.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findBySellerId(Long sellerId);
}
