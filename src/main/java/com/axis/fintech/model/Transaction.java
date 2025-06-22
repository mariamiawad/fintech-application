package com.axis.fintech.model;

import java.time.LocalDateTime;

import com.axis.fintech.utils.IdGenerator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
@Data 
@AllArgsConstructor
public class Transaction {

    public enum TransactionType {
        DEPOSIT,
        WITHDRAWAL
    }
  
    @JsonProperty("transactionId")
    private Long transactionId;
    @JsonProperty("accountId")
    private Long accountId;
    @JsonProperty("amount")
    private Double amount;
    @JsonProperty("type")
    private TransactionType type;
    @JsonProperty("timestamp")
    private LocalDateTime timestamp;
    public Transaction() {}

    public Transaction(double amount, Long accountId, TransactionType type) {
        this.transactionId = IdGenerator.nextTransactionId();
        this.amount = amount;
        this.type = type;
        this.timestamp = LocalDateTime.now();
        this.accountId = accountId;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public Double getAmount() {
        return amount;
    }

    public TransactionType getType() {
        return type;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
