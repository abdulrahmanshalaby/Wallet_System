package com.wallet.marketplace_service.Controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wallet.marketplace_service.Service.ProductService;
import com.wallet.marketplace_service.dtos.ProductDto;
import com.wallet.marketplace_service.security.UserPrincipal;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto dto,
                                                    Authentication authentication) {
        Long sellerId = ((UserPrincipal) authentication.getPrincipal()).getUserId();
        return ResponseEntity.ok(productService.createProduct(sellerId,dto));
    }

    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long id,
                                                    @RequestBody ProductDto dto,
                                                    Authentication authentication) {
        Long sellerId = ((UserPrincipal) authentication.getPrincipal()).getUserId();
        return ResponseEntity.ok(productService.updateProduct(id, dto, sellerId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id,
                                              Authentication authentication) {
        Long sellerId = ((UserPrincipal) authentication.getPrincipal()).getUserId();
        productService.deleteProduct(id, sellerId);
        return ResponseEntity.noContent().build();
    }
}
