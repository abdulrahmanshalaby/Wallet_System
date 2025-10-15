package com.wallet.marketplace_service.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.wallet.marketplace_service.Service.CheckoutService;
import com.wallet.marketplace_service.dtos.CheckoutRequestDto;
import com.wallet.marketplace_service.dtos.OrderDto;
import com.wallet.marketplace_service.security.UserPrincipal;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final CheckoutService checkoutService;

    @PostMapping
    public ResponseEntity<OrderDto> checkout(@RequestBody CheckoutRequestDto request,
                                             Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        // Request contains lat & long
        return ResponseEntity.ok(
                checkoutService.checkout(principal.getUserId(), request)
        );
    }
}
