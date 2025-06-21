package com.axis.fintech.utils;

import com.axis.fintech.model.Account;
import com.axis.fintech.model.Transaction;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class IdGenerator {

    private static AtomicLong accountId = new AtomicLong(1000);
    private static AtomicLong transactionId = new AtomicLong(1);

    static {
        initializeFromData();
    }

    private static void initializeFromData() {
        try {
            List<Account> accounts = JsonFileHandler.loadAccounts();
            List<Transaction> transactions = JsonFileHandler.loadTransactions();

            accounts.stream()
                .map(Account::getAccountId)
                .max(Comparator.naturalOrder())
                .ifPresent(max -> accountId.set(max + 1));

            transactions.stream()
                .map(Transaction::getTransactionId)
                .max(Comparator.naturalOrder())
                .ifPresent(max -> transactionId.set(max + 1));

        } catch (Exception e) {
            System.err.println("1Failed to initialize ID generator from data: " + e.getMessage());
        }
    }

    public static long nextAccountId() {
        return accountId.getAndIncrement();
    }

    public static long nextTransactionId() {
        return transactionId.getAndIncrement();
    }
}
