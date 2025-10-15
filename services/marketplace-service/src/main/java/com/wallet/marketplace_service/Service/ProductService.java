package com.wallet.marketplace_service.Service;
import java.util.List;

import org.springframework.stereotype.Service;

import com.wallet.marketplace_service.Repository.ProductRepository;
import com.wallet.marketplace_service.dtos.ProductDto;
import com.wallet.marketplace_service.model.Product;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public ProductDto createProduct(long Sellerid,ProductDto dto) {
        Product product = Product.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .sellerId(Sellerid)
                .locationCity(dto.getLocationCity())
                .locationCountry(dto.getLocationCountry())
                .build();
        product = productRepository.save(product);

        dto.setId(product.getId());
        return dto;
    }

    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream().map(p ->
                ProductDto.builder()
                        .id(p.getId())
                        .title(p.getTitle())
                        .description(p.getDescription())
                        .price(p.getPrice())
                        .sellerId(p.getSellerId())
                        .locationCity(p.getLocationCity())
                        .locationCountry(p.getLocationCountry())
                        .build()
        ).toList();
    }
    public ProductDto getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        return ProductDto.builder()
                .id(product.getId())
                .title(product.getTitle())
                .description(product.getDescription())
                .price(product.getPrice())
                .sellerId(product.getSellerId())
                .locationCity(product.getLocationCity())
                .locationCountry(product.getLocationCountry())
                .build();
    }
    public void deleteProduct(Long productId, Long sellerId) {
    Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));

    if (!product.getSellerId().equals(sellerId)) {
        throw new RuntimeException("You are not allowed to delete this product");
    }

    productRepository.delete(product);
}
    public ProductDto updateProduct(Long productId, ProductDto dto, Long sellerId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        if (!product.getSellerId().equals(sellerId)) {
        throw new RuntimeException("You are not allowed to update this product");
    }
        product.setTitle(dto.getTitle());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setLocationCity(dto.getLocationCity());
        product.setLocationCountry(dto.getLocationCountry());
        product = productRepository.save(product);
        dto.setId(product.getId());
        return dto;

}
}
