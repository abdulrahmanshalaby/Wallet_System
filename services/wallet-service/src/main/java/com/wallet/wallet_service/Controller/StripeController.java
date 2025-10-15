package com.wallet.wallet_service.Controller;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.wallet.wallet_service.dtos.UserInfo;
import com.wallet.wallet_service.service.StripeService;
import com.wallet.wallet_service.service.WalletService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

@RestController
@RequestMapping("/stripe")
public class StripeController {

    private final StripeService stripeService;

    public StripeController(StripeService stripeService, WalletService walletService) {
        this.stripeService = stripeService;
    }

    // ================= Top-up =================
    @PostMapping("/create-payment-intent")
    public PaymentIntent createPaymentIntent(@RequestAttribute("userInfo") UserInfo userInfo,
                                             @RequestParam BigDecimal amount) throws StripeException {
        return stripeService.createPaymentIntent(userInfo.getUserId(), amount);
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody String payload,
                                                @RequestHeader("Stripe-Signature") String sigHeader) {
        return stripeService.handleStripeWebhook(sigHeader, payload);
    }

    // ================= Realistic Stripe Connect Test =================
    // Create a test connected account for the user
    @PostMapping("/create-test-connected-account")
    public ResponseEntity<String> createTestConnectedAccount(@RequestParam Long userId) throws StripeException {
        String accountId = stripeService.createTestConnectedAccount(userId);
        return ResponseEntity.ok(accountId);
    }

    // Withdraw funds to the user's Stripe connected account (test mode)
    @PostMapping("/withdraw-to-stripe")
    public ResponseEntity<String> withdrawToStripe(@RequestAttribute("userInfo") UserInfo userInfo,
                                                   @RequestParam BigDecimal amount,
                                                   @RequestParam String reference,
                                                   @RequestParam String description,@RequestParam BigDecimal fee)throws StripeException {
        stripeService.processUserWithdrawal(userInfo, amount, reference, description,fee);
        return ResponseEntity.ok("Withdrawal to Stripe initiated for user " + userInfo.getUserId());
    }

}