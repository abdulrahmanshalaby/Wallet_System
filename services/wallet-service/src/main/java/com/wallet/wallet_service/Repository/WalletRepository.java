package com.wallet.wallet_service.Repository;
import com.wallet.wallet_service.model.Wallet;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByUserid(long user_id);

    
} 