package com.wallet.marketplace_service.Service;

import com.wallet.marketplace_service.dtos.CartDto;
import com.wallet.marketplace_service.dtos.CartItemDto;
import com.wallet.marketplace_service.model.Cart;
import com.wallet.marketplace_service.model.CartItem;
import com.wallet.marketplace_service.model.Product;
import com.wallet.marketplace_service.Repository.CartRepository;
import com.wallet.marketplace_service.Repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    // get cart for a buyer
    public CartDto getCart(Long buyerId) {
        Cart cart = cartRepository.findByBuyerId(buyerId)
                .orElse(Cart.builder().buyerId(buyerId).build());

        List<CartItemDto> items = cart.getItems().stream()
                .map(ci -> new CartItemDto(ci.getProduct().getId(), ci.getQuantity()))
                .toList();

        return CartDto.builder()
                .buyerId(buyerId)
                .items(items)
                .build();
    }

    // add item to cart
    @Transactional
    public void addItemToCart(Long buyerId, CartItemDto itemDto) {
        Product product = productRepository.findById(itemDto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Cart cart = cartRepository.findByBuyerId(buyerId)
                .orElse(Cart.builder().buyerId(buyerId).build());

        // check if product already in cart
        Optional<CartItem> existingItemOpt = cart.getItems().stream()
                .filter(ci -> ci.getProduct().getId().equals(product.getId()))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + itemDto.getQuantity());
        } else {
            CartItem cartItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(itemDto.getQuantity())
                    .build();
            cart.getItems().add(cartItem);
        }

        cartRepository.save(cart); // cascades cart items
    }

    // remove item from cart
    @Transactional
    public void removeItemFromCart(Long buyerId, Long productId) {
        Cart cart = cartRepository.findByBuyerId(buyerId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        cart.getItems().removeIf(ci -> ci.getProduct().getId().equals(productId));
        cartRepository.save(cart);
    }

    // clear cart completely
    @Transactional
    public void clearCart(Long buyerId) {
        Cart cart = cartRepository.findByBuyerId(buyerId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        cart.getItems().clear();
        cartRepository.save(cart);
    }
}
