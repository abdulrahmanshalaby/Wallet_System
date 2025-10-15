package com.wallet.wallet_service.dtos;

import com.wallet.wallet_service.model.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionDto {

    private Long id;
    private Long walletId;
    private Long relatedWalletId;
    private TransactionType type;
    private BigDecimal amount;
    private UserInfo user;  // snapshot
    private BigDecimal walletBalanceAfter;
    private String reference;
    private String description;
    private LocalDateTime createdAt;

    // constructor
    public TransactionDto(Long id, Long walletId, Long relatedWalletId,
                          TransactionType type, BigDecimal amount, UserInfo user,
                          BigDecimal walletBalanceAfter, String reference,
                          String description, LocalDateTime createdAt) {
        this.id = id;
        this.walletId = walletId;
        this.relatedWalletId = relatedWalletId;
        this.type = type;
        this.amount = amount;
        this.user = user;
        this.walletBalanceAfter = walletBalanceAfter;
        this.reference = reference;
        this.description = description;
        this.createdAt = createdAt;
    }

    // getters & setters
    public Long getId() { return id; }
    public Long getWalletId() { return walletId; }
    public Long getRelatedWalletId() { return relatedWalletId; }
    public TransactionType getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public UserInfo getUser() { return user; }
    public BigDecimal getWalletBalanceAfter() { return walletBalanceAfter; }
    public String getReference() { return reference; }
    public String getDescription() { return description; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public static TransactionDto fromEntity(com.wallet.wallet_service.model.Transaction tx) {
        return new TransactionDto(
                tx.getId(),
                tx.getWalletId(),
                tx.getRelatedWalletId(),
                tx.getType(),
                tx.getAmount(),
                tx.getUser(),
                tx.getWalletBalanceAfter(),
                tx.getReference(),
                tx.getDescription(),
                tx.getCreatedAt()
        );
    }
}
