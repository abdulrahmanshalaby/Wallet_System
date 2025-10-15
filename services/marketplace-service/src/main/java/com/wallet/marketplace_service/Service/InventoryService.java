package com.wallet.marketplace_service.Service;
import org.springframework.stereotype.Service;

import com.wallet.marketplace_service.Repository.InventoryRepository;
import com.wallet.marketplace_service.Repository.ProductRepository;
import com.wallet.marketplace_service.dtos.InventoryDto;
import com.wallet.marketplace_service.model.Inventory;
import com.wallet.marketplace_service.model.Product;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    // add stock for existing product (creates Inventory if not exists)
    public InventoryDto addStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Inventory inv = inventoryRepository.findByProduct(product)
                .orElse(Inventory.builder()
                        .product(product)
                        .availableQuantity(0)
                        .reservedQuantity(0)
                        .build());

        inv.setAvailableQuantity(inv.getAvailableQuantity() + quantity);
        inventoryRepository.save(inv);

        return new InventoryDto(productId, inv.getAvailableQuantity(), inv.getReservedQuantity());
    }

    public InventoryDto getInventory(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        Inventory inv = inventoryRepository.findByProduct(product)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));

        return new InventoryDto(productId, inv.getAvailableQuantity(), inv.getReservedQuantity());
    }

}
