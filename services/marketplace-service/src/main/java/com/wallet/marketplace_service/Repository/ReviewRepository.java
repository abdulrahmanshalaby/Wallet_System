package com.wallet.marketplace_service.Repository;

import com.wallet.marketplace_service.model.Review;
import com.wallet.marketplace_service.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByOrderItem(OrderItem orderItem);
}
