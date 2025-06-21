package com.axis.fintech.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.axis.fintech.model.Account;
import com.axis.fintech.model.Transaction;
import com.axis.fintech.model.Transaction.TransactionType;
import com.axis.fintech.utils.JsonFileHandler;

import jakarta.annotation.PostConstruct;
@Service
public class TransactionService {

    private List<Transaction> transactions;
    private final AccountService accountService;

    public TransactionService(AccountService accountService) {
        this.accountService = accountService;

    }
    @PostConstruct
    public void init() {
        try {
            this.transactions = JsonFileHandler.loadTransactions();
        } catch (Exception e) {
            this.transactions = new ArrayList<>();
            System.err.println("Failed to load transactions: " + e.getMessage());
        }
    }

    public Long deposit(String userName, double amount) {
        Account account = accountService.findByUserName(userName);
        if (account == null) {
        	System.out.println("user doesn't exits please an account first");
        	return null;
        }

        account.setBalance(account.getBalance() + amount);

        Transaction tx = new Transaction(amount, account.getAccountId(), TransactionType.DEPOSIT);
        transactions.add(tx);
        account.addTransactionId(tx.getTransactionId());

        accountService.updateAccount(account);
        JsonFileHandler.saveTransactions(transactions);
        return tx.getTransactionId();
    }

    public Long withdraw(String userName, double amount) {
        Account account = accountService.findByUserName(userName);
        if (account == null) {
        	System.out.println("user doesn't exists please open an account first");
        	return null;
        }
        if(account.getBalance() < amount) {
        	System.out.println("You don't have enough balance");
        	return null;
        }

        account.setBalance(account.getBalance() - amount);

        Transaction tx = new Transaction(amount, account.getAccountId(), TransactionType.WITHDRAWAL);
        transactions.add(tx);
        account.addTransactionId(tx.getTransactionId());

        accountService.updateAccount(account);
        JsonFileHandler.saveTransactions(transactions);
        System.out.println("Transaction number is " + tx.getTransactionId());
        return tx.getTransactionId();
    }
    
    public List<Transaction> getAllTransactions() {
        return transactions;
    }
}
