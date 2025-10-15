package com.wallet.marketplace_service.Repository;

import com.wallet.marketplace_service.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {}