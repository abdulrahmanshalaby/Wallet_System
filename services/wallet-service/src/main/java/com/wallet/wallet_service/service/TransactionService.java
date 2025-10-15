package com.wallet.wallet_service.service;

import com.wallet.wallet_service.model.Transaction;
import com.wallet.wallet_service.model.Wallet;

import com.wallet.wallet_service.model.TransactionType;
import com.wallet.wallet_service.model.TransactionStatus;

import com.wallet.wallet_service.Repository.TransactionRepository;
import com.wallet.wallet_service.Repository.WalletRepository;
import com.wallet.wallet_service.dtos.UserInfo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository txRepo;
    private final WalletRepository walletRepo;

    public TransactionService(TransactionRepository txRepo, WalletRepository walletRepo) {
        this.txRepo = txRepo;
        this.walletRepo = walletRepo;
    }

    public Transaction recordTransaction(Long walletId,
                                         Long relatedWalletId,
                                         TransactionType type,
                                         BigDecimal amount,
                                         BigDecimal walletBalanceAfter,
                                         String reference,
                                         String description,UserInfo user,
                                         TransactionStatus status
                                         ) {
        // if we have a reference, try to find it to ensure idempotency
        if (reference != null) {
            return txRepo.findByReference(reference)
                    .orElseGet(() ->
                            saveTx(walletId, relatedWalletId, type, amount, walletBalanceAfter, reference, description,user,status)
                    );
        }
        return saveTx(walletId, relatedWalletId, type, amount, walletBalanceAfter, null, description,user,status);
    }

    private Transaction saveTx(Long walletId,
                               Long relatedWalletId,
                               TransactionType type,
                               BigDecimal amount,
                               BigDecimal walletBalanceAfter,
                               String reference,
                               String description,
                               UserInfo user
                                 ,TransactionStatus status
                               ) {
        Transaction tx = new Transaction();
        tx.setWalletId(walletId);
        tx.setRelatedWalletId(relatedWalletId);
        tx.setType(type);
        tx.setAmount(amount);
        tx.setWalletBalanceAfter(walletBalanceAfter);
        tx.setReference(reference);
        tx.setUser(user);
        tx.setDescription(description);
        tx.setCreatedAt(LocalDateTime.now());
        tx.setStatus(status);
        return txRepo.save(tx);
    }

    public Page<Transaction> getTransactionsForWallet(Long walletId, Pageable pageable) {
        return txRepo.findByWalletIdOrderByCreatedAtDesc(walletId, pageable);
    }
     public List<Transaction> getTransactionsForUser(Long userId) {
        // 1. Get the wallet by userId
        Wallet wallet = walletRepo.findByUserid(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found for user " + userId));

        // 2. Fetch transactions for that wallet
        // (without pageable)
        return txRepo.findByWalletIdOrderByCreatedAtDesc(wallet.getId(), Pageable.unpaged()).getContent();
    }
}
