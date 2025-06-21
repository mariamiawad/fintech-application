package com.axis.fintech.cli;

import com.axis.fintech.service.AccountService;
import com.axis.fintech.service.TransactionService;

import java.util.Scanner;

public class MenuService {

	private final AccountService accountService;
	private final TransactionService transactionService;
	private final Runnable exitAction;
	private boolean running = true;

	public MenuService(AccountService accountService, TransactionService transactionService) {
		this(accountService, transactionService, () -> System.exit(0));
	}

	public MenuService(AccountService accountService, TransactionService transactionService, Runnable exitAction) {
		this.accountService = accountService;
		this.transactionService = transactionService;
		this.exitAction = exitAction;
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
				exitAction.run();
			}
			default -> System.err.println("Invalid choice.");
			}
			if(running) {
				boolean toContinue = toContinue(scanner);
				if (!toContinue) {
					running = false;
					exitAction.run();
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
			if (!toContinue(scanner)) {
				exitAction.run();
				running = false;
			}
		}
	}

	private void withdraw(Scanner scanner, String userName) {
		System.out.print("Enter amount: ");
		try {
			double amt = Double.parseDouble(scanner.nextLine());
			long transactionId = transactionService.withdraw(userName, amt);
			System.out.println("Transaction number is " + transactionId);
		} catch (Exception e) {
			System.err.println("Amount should be a number");
		}
	}

	private void checkBalance(Scanner scanner, String userName) {
		double balance = accountService.getBalance(userName);
		System.out.println("Your balance is " + balance);
	}

	private boolean toContinue(Scanner scanner) {
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
