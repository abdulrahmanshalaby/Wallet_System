package com.wallet.wallet_service.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Payout;
import com.stripe.model.Transfer;
import com.stripe.model.checkout.Session;
import com.stripe.net.RequestOptions;
import com.stripe.net.Webhook;
import com.stripe.model.Account;
import com.wallet.wallet_service.Client.AuthClient;
import com.wallet.wallet_service.dtos.UserInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class StripeService {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;
    @Value("${stripe.endpoint.secret}")
    private String endpointSecret;

    private final WalletService walletService;
    private final AuthClient authClient;

    // Store connected account IDs per user
    private final Map<Long, String> connectedAccounts = new ConcurrentHashMap<>();

    public StripeService(WalletService walletService, AuthClient authClient) {
        this.walletService = walletService;
        this.authClient = authClient;
    }

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    // ================= Top-up / Checkout =================
    public PaymentIntent createPaymentIntent(Long userId, BigDecimal amount) throws StripeException {
        long amountInCents = amount.multiply(BigDecimal.valueOf(100)).longValue();

        Map<String, Object> metadata = Map.of("userId", userId.toString());

        Map<String, Object> params = new HashMap<>();
        params.put("amount", amountInCents);
        params.put("currency", "usd");
        params.put("metadata", metadata);

        // ✅ Platform receives full amount; no automatic transfer to connected account
        return PaymentIntent.create(params);
    }

    public PaymentIntent retrievePaymentIntent(String paymentIntentId) throws StripeException {
        return PaymentIntent.retrieve(paymentIntentId);
    }

    public Session createCheckoutSession(Long userId, BigDecimal amount, String successUrl, String cancelUrl) throws StripeException {
        Map<String, Object> params = new HashMap<>();
        params.put("mode", "payment");
        params.put("success_url", successUrl);
        params.put("cancel_url", cancelUrl);

        Map<String, Object> lineItem = new HashMap<>();
        lineItem.put("price_data", Map.of(
                "currency", "usd",
                "product_data", Map.of("name", "Wallet top-up"),
                "unit_amount", amount.multiply(BigDecimal.valueOf(100)).longValue()
        ));
        lineItem.put("quantity", 1);

        params.put("line_items", java.util.List.of(lineItem));
        params.put("metadata", Map.of("userId", userId.toString()));

        return Session.create(params);
    }

    public ResponseEntity<String> handleStripeWebhook(String sigHeader, String payload) {
        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid signature");
        }

        switch (event.getType()) {
            case "payment_intent.succeeded" -> {
                PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer()
                        .getObject().orElse(null);
                if (paymentIntent != null) processPaymentSuccess(paymentIntent);
            }
        }

        return ResponseEntity.ok("success");
    }

    private void processPaymentSuccess(PaymentIntent paymentIntent) {
        String userIdStr = paymentIntent.getMetadata().get("userId");
        if (userIdStr == null) return;
        String reference = paymentIntent.getId();
        String description = "Wallet top-up via Stripe";
        Long userId = Long.valueOf(userIdStr);
        UserInfo userInfo = authClient.getUserInfoById(userId);
        BigDecimal amount = BigDecimal.valueOf(paymentIntent.getAmount()).divide(BigDecimal.valueOf(100));

        // ✅ Credit internal wallet only
        walletService.creditWallet(userInfo, amount, reference, description);
    }

    // ================= Stripe Connect Test Accounts =================
    public String createTestConnectedAccount(Long userId) throws StripeException {
        Map<String, Object> accountParams = new HashMap<>();
        accountParams.put("type", "custom");
        accountParams.put("country", "US");
        accountParams.put("email", "user" + userId + "@example.com");
        accountParams.put("capabilities", Map.of("transfers", Map.of("requested", true)));

        Account account = Account.create(accountParams);

        // Attach a test bank account
        Map<String, Object> bankAccount = new HashMap<>();
        bankAccount.put("object", "bank_account");
        bankAccount.put("country", "US");
        bankAccount.put("currency", "usd");
        bankAccount.put("account_number", "000123456789");
        bankAccount.put("routing_number", "110000000");

        account.getExternalAccounts().create(bankAccount);

        connectedAccounts.put(userId, account.getId());
        System.out.println("Created test connected account " + account.getId() + " for user " + userId);
        return account.getId();
    }

    // ================= User Withdrawal =================
    public void processUserWithdrawal(UserInfo userInfo, BigDecimal amount, String reference, String description, BigDecimal platformFee) {
        // 1️⃣ Request withdrawal in wallet (marks it as pending)
        var tx = walletService.requestWithdrawal(userInfo, amount, reference, description);

        // 2️⃣ Deduct platform fee
        BigDecimal netAmount = amount.subtract(platformFee);

        String connectedAccountId = connectedAccounts.get(userInfo.getUserId());
        if (connectedAccountId == null) {
            throw new RuntimeException("Connected account not found for user " + userInfo.getUserId());
        }

        try {
            long netAmountInCents = netAmount.multiply(BigDecimal.valueOf(100)).longValue();

            // 3️⃣ Transfer netAmount from platform balance → connected account
            Map<String, Object> transferParams = new HashMap<>();
            transferParams.put("amount", netAmountInCents);
            transferParams.put("currency", "usd");
            transferParams.put("destination", connectedAccountId);

            Transfer.create(transferParams);

            // 4️⃣ Payout from connected account → bank
            Map<String, Object> payoutParams = new HashMap<>();
            payoutParams.put("amount", netAmountInCents);
            payoutParams.put("currency", "usd");

            Payout.create(payoutParams, RequestOptions.builder()
                    .setStripeAccount(connectedAccountId)
                    .build());

            // 5️⃣ Mark wallet transaction as completed
            walletService.markTransactionPaidOut(tx.getId().toString());

            System.out.println("Withdrawal completed: $" + netAmount + " for user " + userInfo.getUserId());

        } catch (StripeException e) {
            throw new RuntimeException("Stripe payout failed: " + e.getMessage(), e);
        }
    }
}
