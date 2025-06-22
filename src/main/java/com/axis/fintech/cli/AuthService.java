package com.axis.fintech.cli;

import java.io.Console;
import java.util.Scanner;
import java.util.function.Supplier;

import com.axis.fintech.model.Account;
import com.axis.fintech.service.AccountService;
import com.axis.fintech.utils.Exiter;

public class AuthService {
	private final Exiter exiter;
    private final AccountService accountService;
    private String password;

    public AuthService(AccountService accountService, Exiter exiter) {
        this.accountService = accountService;
        this.exiter =  exiter;
    }

    public String login(Scanner scanner) {
        System.out.print("Enter user name: ");
        String userName = scanner.nextLine();

        Account existing = accountService.findByUserName(userName);
        if (existing == null) {
            System.err.println("You don't have an account!");
            return null;
        }

        boolean isValid = false;
        while (!isValid) {
            password = readPassword(scanner);
            isValid = accountService.validatePassword(userName, password);
            if (!isValid) {
                System.err.println("Incorrect password. Try again.");
            }
        }
        return userName;
    }

    public String signup(Scanner scanner) {
        System.out.println("Open Account");
        System.out.print("Enter user name: ");
        String userName = scanner.nextLine();
        password = readPassword(scanner);
        Long accountId = accountService.openAccount(userName, password);
        if (accountId != null) {
            System.out.println("Your account id is " + accountId);
            return userName;
        }
        return retry(scanner, ()-> signup(scanner));
    }
    
    public String readPassword(Scanner scanner) {
        Console console = System.console();
        if (console != null) {
            char[] passChars = console.readPassword("Enter password: ");
            return new String(passChars);
        } else {
            System.out.println("Cannot hide password in this environment.");
            System.out.print("Enter password (visible): ");
            return scanner.nextLine();
        }
    }
    
    public String retry(Scanner scanner, Supplier<String> retryFunction) {
        System.out.print("Would you like to return to main menu? (yes/no): ");
        String retry = scanner.nextLine().trim().toLowerCase();

        while (!retry.matches("^(yes|y|no|n)$")) {
            System.out.print("Please enter yes/y or no/n: ");
            retry = scanner.nextLine().trim().toLowerCase();
        }

        if (retry.equals("yes") || retry.equals("y")) {
            return retryFunction.get();
        } else {
            System.out.println("Exiting...");
            exiter.exit(0);
            return null;
        }
    }
    public boolean retry(Scanner scanner, Runnable retryFunction) {
        System.out.print("Would you like to return to main menu? (yes/no): ");
        String retry = scanner.nextLine().trim().toLowerCase();

        while (!retry.matches("^(yes|y|no|n)$")) {
            System.out.print("Please enter yes/y or no/n: ");
            retry = scanner.nextLine().trim().toLowerCase();
            
        }

        if (retry.equals("yes") || retry.equals("y")) {
            return true;
        } else {
            System.out.println("Exiting...");
            exiter.exit(0);
            return false;
        }
    }
}
