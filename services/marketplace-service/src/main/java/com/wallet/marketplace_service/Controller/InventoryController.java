package com.wallet.marketplace_service.controller;

import com.wallet.marketplace_service.Service.InventoryService;
import com.wallet.marketplace_service.Service.ProductService;
import com.wallet.marketplace_service.dtos.InventoryDto;
import com.wallet.marketplace_service.dtos.ProductDto;
import com.wallet.marketplace_service.model.Product;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;
    private final ProductService productService; // to check product ownership

    // ✅ Get inventory info for a product
    @GetMapping("/{productId}")
    public ResponseEntity<InventoryDto> getInventory(@PathVariable Long productId) {
        InventoryDto dto = inventoryService.getInventory(productId);
        return ResponseEntity.ok(dto);
    }

    // ✅ Add stock (or create inventory record)
    @PostMapping("/{productId}/add-stock")
    public ResponseEntity<InventoryDto> addStock(
            @PathVariable Long productId,
            @RequestParam int quantity,
            Authentication authentication) {

        // extract sellerId from JWT/Authentication
        Long sellerId = Long.parseLong(authentication.getName());

        // check product belongs to this seller before allowing stock change
        Product product = productService.getProductEntity(productId);
        if (!product.getSellerId().equals(sellerId)) {
            return ResponseEntity.status(403).build();
        }

        InventoryDto dto = inventoryService.addStock(productId, quantity);
        return ResponseEntity.ok(dto);
    }

    // ✅ Update inventory quantities explicitly (optional)
    @PutMapping("/{productId}")
    public ResponseEntity<InventoryDto> updateInventory(
            @PathVariable Long productId,
            @RequestParam int availableQuantity,
            @RequestParam int reservedQuantity,
            Authentication authentication) {

        Long sellerId = Long.parseLong(authentication.getName());
        ProductDto product = productService.getProductById(productId);
        if (!product.getSellerId().equals(sellerId)) {
            return ResponseEntity.status(403).build();
        }

        InventoryDto dto = inventoryService.updateInventory(productId, availableQuantity, reservedQuantity);
        return ResponseEntity.ok(dto);
    }

    // ✅ Delete inventory entry for a product
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteInventory(@PathVariable Long productId, Authentication authentication) {
        Long sellerId = Long.parseLong(authentication.getName());
        ProductDto product = productService.getProductById(productId);
        if (!product.getSellerId().equals(sellerId)) {
            return ResponseEntity.status(403).build();
        }

        inventoryService.deleteInventory(productId);
        return ResponseEntity.noContent().build();
    }
}
