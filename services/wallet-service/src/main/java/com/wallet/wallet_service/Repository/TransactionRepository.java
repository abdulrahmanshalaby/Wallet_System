package com.wallet.wallet_service.Repository;
import com.wallet.wallet_service.model.Transaction;

import java.util.Optional;


import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByWalletId(long id);
    Optional<Transaction> findByReference(String reference);
    org.springframework.data.domain.Page<Transaction> findByWalletIdOrderByCreatedAtDesc(Long walletId, org.springframework.data.domain.Pageable pageable);
    // List<Transaction> findByUserUserIdOrderByCreatedAtDesc(Long userId);
}
