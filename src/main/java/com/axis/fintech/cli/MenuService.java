package com.axis.fintech.cli;

import java.util.Scanner;

import com.axis.fintech.service.AccountService;
import com.axis.fintech.service.TransactionService;
import com.axis.fintech.utils.Exiter;

public class MenuService {

	private final AccountService accountService;
	private final TransactionService transactionService;
	private final Exiter exiter;
	private boolean running = true;

	public MenuService(AccountService accountService, TransactionService transactionService) {
		this(accountService, transactionService, status -> System.exit(status));
	}

	public MenuService(AccountService accountService, TransactionService transactionService, Exiter exiter) {
		this.accountService = accountService;
		this.transactionService = transactionService;
		this.exiter = exiter;
	}

	public void menu(Scanner scanner, String userName) {
		while (running) {
			System.out.println("\n=== Fintech CLI Menu ===");
			System.out.println("1. Deposit");
			System.out.println("2. Withdraw");
			System.out.println("3. Check Balance");
			System.out.println("4. Exit");
			System.out.print("Enter choice: ");
			int choice;
			try {
				choice = Integer.parseInt(scanner.nextLine());
			} catch (Exception e) {
				System.err.println("Invalid input. Please enter a number.");
				continue;
			}

			switch (choice) {
			case 1 -> deposit(scanner, userName);
			case 2 -> withdraw(scanner, userName);
			case 3 -> checkBalance(scanner, userName);
			case 4 -> {
				System.out.println("Goodbye!");
				running = false;
				exiter.exit(0);
			}
			default -> System.err.println("Invalid choice.");
			}
			if (running) {
				boolean toContinue = toContinue(scanner);
				if (!toContinue) {
					running = false;
					exiter.exit(0);
				}
			}

		}
	}

	private void deposit(Scanner scanner, String userName) {
		System.out.print("Enter amount: ");
		try {
			double amt = Double.parseDouble(scanner.nextLine());
			long transactionId = transactionService.deposit(userName, amt);
			System.out.println("Transaction number is " + transactionId);
		} catch (Exception e) {
			System.err.println("Amount should be a number");
			return;
		}
	}

	private void withdraw(Scanner scanner, String userName) {
		System.out.print("Enter amount: ");
		try {
			double amt = Double.parseDouble(scanner.nextLine());
			Long transactionId = transactionService.withdraw(userName, amt);
			if (transactionId != null) {
				System.out.println("Transaction number is " + transactionId);
			} else {
				return;
			}
		} catch (Exception e) {
			System.err.println("Amount should be a number");
			return;

		}
	}

	private void checkBalance(Scanner scanner, String userName) {
		double balance = accountService.getBalance(userName);
		System.out.println("Your balance is " + balance);
	}

	public boolean toContinue(Scanner scanner) {
		String answer;
		while (true) {
			System.out.print("Do you want to continue? (yes/y or no/n): ");
			answer = scanner.nextLine().trim().toLowerCase();
			if (answer.equals("yes") || answer.equals("y"))
				return true;
			if (answer.equals("no") || answer.equals("n")) {
				System.out.println("Goodbye!");
				return false;
			}
			System.err.println("Invalid input. Please enter yes/y or no/n.");

		}
	}
}
