package com.axis.fintech.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Account {
    @JsonProperty("accountId")
    private Long accountId;
    
    @JsonProperty("userName")
    private String userName;
    
    @JsonProperty("balance")
    private Double balance;
    
    @JsonProperty("timestamp")
    private LocalDateTime timestamp;
    
    @JsonProperty("passwordHash")
    private String passwordHash; 
    
    private List<Long> transactions = new ArrayList<>();
    
    public Account() {}

    public Account(String userName, Long accountId, Double balance,String passwordHash) {
        this.userName = userName;
        this.accountId = accountId;
        this.balance = balance;
        this.passwordHash = passwordHash;
        this.timestamp = LocalDateTime.now();
    }
	
    public Long getAccountId() {
        return accountId;
    }
    
    public String getUserName() {
        return userName;
    }

    public Double getBalance() {
        return balance;
    }

    public List<Long> getTransactions() {
        return transactions;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }
    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    public void setTransactions(List<Long> transactions) {
        this.transactions = transactions;
    }

    public void addTransactionId(Long txId) {
        this.transactions.add(txId);
    }
    public LocalDateTime getTimestamp() {
		return timestamp;
	}
    public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}
}
