package com.wallet.wallet_service.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.wallet.wallet_service.dtos.UserInfo;

import jakarta.persistence.*;
@Entity
@Table(name = "wallets")

public class Transaction {

    @Id @GeneratedValue
    private Long id;

    private Long walletId;
    private Long relatedWalletId;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    private BigDecimal amount;
    private UserInfo user;

    // This is the balance of the wallet after the transaction
    private BigDecimal walletBalanceAfter;

    private String reference;
    private String description;

    private LocalDateTime createdAt;
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getWalletId() { return walletId; }
    public void setWalletId(Long walletId) { this.walletId = walletId; }
    public Long getRelatedWalletId() { return relatedWalletId; }
    public void setRelatedWalletId(Long relatedWalletId) { this.relatedWalletId = relatedWalletId; }
    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public UserInfo getUser() { return user; }
    public void setUser(UserInfo user) { this.user = user; }
    public BigDecimal getWalletBalanceAfter() { return walletBalanceAfter; }
    public void setWalletBalanceAfter(BigDecimal walletBalanceAfter) { this.walletBalanceAfter = walletBalanceAfter; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public TransactionStatus getStatus() { return status; }
    public void setStatus(TransactionStatus status) { this.status = status; }
        

}
