package com.wallet.marketplace_service.Repository;
import com.wallet.marketplace_service.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByBuyerId(Long buyerId);
}
