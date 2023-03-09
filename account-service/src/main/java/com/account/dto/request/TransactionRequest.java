package com.account.dto.request;

import java.math.BigDecimal;

public class TransactionRequest {

    private String accountOrigin;

    private String accountDestination;

    private BigDecimal amount;

    private String cardNumber;

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public TransactionRequest() {
    }

    public String getAccountOrigin() {
        return accountOrigin;
    }

    public void setAccountOrigin(String accountOrigin) {
        this.accountOrigin = accountOrigin;
    }

    public String getAccountDestination() {
        return accountDestination;
    }

    public void setAccountDestination(String accountDestination) {
        this.accountDestination = accountDestination;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "TransactionRequest{" +
                "accountOrigin='" + accountOrigin + '\'' +
                ", accountDestination='" + accountDestination + '\'' +
                ", amount=" + amount +
                ", cardNumber='" + cardNumber + '\'' +
                '}';
    }
}
