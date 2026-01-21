package com.win777.backend.dto;

import java.math.BigDecimal;

/**
 * DTO for wallet balance response.
 */
public class WalletBalanceResponse {

    private BigDecimal balance;

    public WalletBalanceResponse() {
    }

    public WalletBalanceResponse(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
