package com.wallet.marketplace_service.Controller;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wallet.marketplace_service.Service.CartService;
import com.wallet.marketplace_service.dtos.CartDto;
import com.wallet.marketplace_service.dtos.CartItemDto;
import com.wallet.marketplace_service.security.UserPrincipal;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/items")
    public ResponseEntity<CartDto> addToCart(@RequestBody CartItemDto dto,
                                             Authentication authentication) {
        Long buyerId = ((UserPrincipal) authentication.getPrincipal()).getUserId();
        cartService.addItemToCart(buyerId, dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<CartDto> getCart(Authentication authentication) {
        Long buyerId = ((UserPrincipal) authentication.getPrincipal()).getUserId();
        return ResponseEntity.ok(cartService.getCart(buyerId));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> removeFromCart(@PathVariable Long itemId,
                                               Authentication authentication) {
        Long buyerId = ((UserPrincipal) authentication.getPrincipal()).getUserId();
        cartService.removeItemFromCart(buyerId, itemId);
        return ResponseEntity.noContent().build();
    }
}
