package com.wallet.wallet_service.Controller;

import com.wallet.wallet_service.Client.AuthClient;
import com.wallet.wallet_service.dtos.UserInfo;
import com.wallet.wallet_service.model.Wallet;
import com.wallet.wallet_service.model.Transaction;
import com.wallet.wallet_service.service.WalletService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/wallets")
public class WalletController {
    private final AuthClient authClient; // Assume this client fetches user info from auth service
    private final WalletService walletService;

    public WalletController(WalletService walletService, AuthClient authClient) {
        this.walletService = walletService;
        this.authClient = authClient;
    }

    // Get my wallet
    @GetMapping("/me")
    public Wallet getMyWallet(@RequestAttribute("userInfo") UserInfo userInfo) {
        return walletService.getWalletByUserId(userInfo.getUserId());
    }

    // Top-up wallet (Stripe handled separately)
    @PostMapping("/me/top-up")
    public void topUpWallet(@RequestAttribute("userInfo") UserInfo userInfo,
                            @RequestParam BigDecimal amount,
                            @RequestParam String reference,
                            @RequestParam String description) {
        walletService.creditWallet(userInfo, amount, reference, description);
    }

    // Withdraw wallet (Stripe handled separately)
     @PostMapping("/me/withdraw")
    public Transaction withdrawWallet(@RequestAttribute("userInfo") UserInfo userInfo,
                                      @RequestParam BigDecimal amount,
                                      @RequestParam String reference,
                                      @RequestParam String description) {
        return walletService.requestWithdrawal(userInfo, amount, reference, description);
    }

    // Admin simulates manual payouts
    @PostMapping("/admin/process-payouts")
    public void processPayouts() {
        walletService.processPendingPayouts();
    }

    // View pending withdrawals
    @GetMapping("/admin/pending-withdrawals")
    public List<Transaction> pendingWithdrawals() {
        return walletService.getPendingWithdrawals();
    }

    // Transfer to another user
    @PostMapping("/me/transfer/{toUserId}")
    public void transfer(@RequestAttribute("userInfo") UserInfo userInfo,
                         @PathVariable Long toUserId,
                         @RequestParam BigDecimal amount,
                         @RequestParam String reference,
                         @RequestParam String description) {
        // Assuming you can fetch recipient info (from JWT or Auth)
        UserInfo toUser = authClient.getUserInfoById(toUserId);
        walletService.transfer(userInfo, toUser, amount, reference, description);
    }
}
