package com.wallet.wallet_service.Controller;

import com.wallet.wallet_service.model.Transaction;
import com.wallet.wallet_service.dtos.TransactionDto;
import com.wallet.wallet_service.dtos.UserInfo;
import com.wallet.wallet_service.service.TransactionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService txService;

    public TransactionController(TransactionService txService) {
        this.txService = txService;
    }

    // Get my transactions
  @GetMapping("/me")
    public List<TransactionDto> getMyTransactions(
            @RequestAttribute("userInfo") UserInfo userInfo) {

        // Service already knows how to get wallet → just return DTOs
        List<Transaction> txList = txService.getTransactionsForUser(userInfo.getUserId());

        return txList.stream()
                .map(TransactionDto::fromEntity)
                .toList();
    }
    // Optional: Get transaction by ID
   
}
