package com.wallet.auth_service.Repository;


import com.wallet.auth_service.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;


public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
Optional<VerificationToken> findByToken(String token);
}