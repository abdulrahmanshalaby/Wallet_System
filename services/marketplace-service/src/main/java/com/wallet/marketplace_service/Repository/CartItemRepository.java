package com.wallet.marketplace_service.Repository;

import com.wallet.marketplace_service.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    void deleteByCartId(Long cartId);
}
