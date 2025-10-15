package com.wallet.wallet_service.model;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;
@Entity
@Table(name = "wallets")
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;    
    @Column(name = "user_id", unique = true, nullable = false)
    private long user_id;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
    


    public Wallet() {}

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    public long getUser_id() { return user_id; }
    public void setUser_id(long user_id) { this.user_id = user_id; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    
}
