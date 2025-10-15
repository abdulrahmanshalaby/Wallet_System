package com.wallet.wallet_service.model;
public enum TransactionStatus {
    PENDING,      // request received, not processed
    COMPLETED,    // balance updated (deducted or added)
    PAID_OUT      // for withdrawals, money sent
}
