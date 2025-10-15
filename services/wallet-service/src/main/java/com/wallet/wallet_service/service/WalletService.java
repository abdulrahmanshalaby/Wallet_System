package com.wallet.wallet_service.service;

import com.wallet.wallet_service.model.Wallet;
import com.wallet.wallet_service.model.Transaction;
import com.wallet.wallet_service.model.TransactionStatus;
import com.wallet.wallet_service.model.TransactionType;
import com.wallet.wallet_service.dtos.UserInfo;
import com.wallet.wallet_service.Repository.TransactionRepository;
import com.wallet.wallet_service.Repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class WalletService {

    private final WalletRepository walletRepo;
    private final TransactionService txService;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public WalletService(WalletRepository walletRepo, TransactionService txService,
                         WalletRepository walletRepository,
                         TransactionRepository transactionRepository) {
        this.walletRepo = walletRepo;
        this.txService = txService;
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    public Wallet createWalletForUser(UserInfo user) {
        return walletRepo.findByUserid(user.getUserId())
                .orElseGet(() -> {
                    Wallet wallet = new Wallet();
                    wallet.setUser_id(user.getUserId());
                    return walletRepo.save(wallet);
                });
    }

    public Wallet getWalletByUserId(Long userId) {
        return walletRepo.findByUserid(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found for user " + userId));
    }

    @Transactional
    public void creditWallet(UserInfo user, BigDecimal amount, String reference, String description) {
        Wallet wallet = getWalletByUserId(user.getUserId());
        wallet.setBalance(wallet.getBalance().add(amount));
        wallet = walletRepo.save(wallet);

        txService.recordTransaction(
                wallet.getId(),
                null,
                TransactionType.TOP_UP,
                amount,
                wallet.getBalance(),
                reference,
                description,
                user,
                TransactionStatus.COMPLETED
        );
    }

    @Transactional
    public void transfer(UserInfo fromUser, UserInfo toUser, BigDecimal amount, String reference, String description) {
        Wallet from = getWalletByUserId(fromUser.getUserId());
        Wallet to = getWalletByUserId(toUser.getUserId());

        if (from.getBalance().compareTo(amount) < 0)
            throw new RuntimeException("Insufficient balance");

        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));

        from = walletRepo.save(from);
        to = walletRepo.save(to);

        txService.recordTransaction(
                from.getId(),
                to.getId(),
                TransactionType.TRANSFER_OUT,
                amount,
                from.getBalance(),
                reference,
                description,
                fromUser,
                TransactionStatus.COMPLETED
        );

        txService.recordTransaction(
                to.getId(),
                from.getId(),
                TransactionType.TRANSFER_IN,
                amount,
                to.getBalance(),
                reference,
                description,
                toUser,
                TransactionStatus.COMPLETED
        );
    }

    // User requests withdrawal
    @Transactional
    public Transaction requestWithdrawal(UserInfo user, BigDecimal amount, String reference, String description) {
        Wallet wallet = walletRepository.findById(user.getUserId())
                .orElseGet(() -> {
                    Wallet w = new Wallet();
                    w.setUser_id(user.getUserId());
                    return walletRepo.save(w);
                });

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        walletRepository.save(wallet);

        Transaction tx = txService.recordTransaction(
                wallet.getId(),
                null,
                TransactionType.WITHDRAWAL,
                amount,
                wallet.getBalance(),
                reference,
                description,
                user,
                TransactionStatus.PENDING
        );

        System.out.println("Withdrawal requested: " + amount + " for user " + user.getUserId());
        return tx;
    }

    // Mark a withdrawal as paid (mock Stripe payout)
    @Transactional
    public void markTransactionPaidOut(String transactionId) {
        Transaction tx = transactionRepository.findById(Long.valueOf(transactionId))
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (tx.getType() != TransactionType.WITHDRAWAL) return;

        tx.setStatus(TransactionStatus.COMPLETED);
        transactionRepository.save(tx);
        System.out.println("Transaction " + transactionId + " marked as paid (mock payout)");
    }

    // Simulate sending money from “our bank” to users
    public void processPendingPayouts() {
        List<Transaction> pendingWithdrawals = transactionRepository.findAll()
                .stream()
                .filter(tx -> tx.getType() == TransactionType.WITHDRAWAL
                        && tx.getStatus() == TransactionStatus.PENDING)
                .toList();

        for (Transaction tx : pendingWithdrawals) {
            System.out.println("Sending " + tx.getAmount() + " to user " + tx.getUser().getUserId() + " (simulate bank transfer)");
            tx.setStatus(TransactionStatus.COMPLETED);
            transactionRepository.save(tx);
        }
    }

    // Check pending withdrawals
    public List<Transaction> getPendingWithdrawals() {
        return transactionRepository.findAll()
                .stream()
                .filter(tx -> tx.getType() == TransactionType.WITHDRAWAL && tx.getStatus() == TransactionStatus.PENDING)
                .toList();
    }
}
