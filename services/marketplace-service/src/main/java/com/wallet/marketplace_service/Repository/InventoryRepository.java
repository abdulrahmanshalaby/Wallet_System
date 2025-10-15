package com.wallet.marketplace_service.Repository;

import com.wallet.marketplace_service.model.Inventory;
import com.wallet.marketplace_service.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByProduct(Product product);
}
