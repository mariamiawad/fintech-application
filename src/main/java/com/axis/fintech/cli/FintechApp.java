package com.axis.fintech.cli;

import java.util.Scanner;

import org.springframework.stereotype.Component;

import com.axis.fintech.model.Account;
import com.axis.fintech.service.AccountService;
import com.axis.fintech.service.TransactionService;
@Component
public class FintechApp {

	private final AuthService authService;
	private final MenuService menuService;
	private final AccountService accountService;
	public FintechApp(AccountService accountService, TransactionService transactionService) {
		this.authService = new AuthService(accountService);
		this.menuService = new MenuService(accountService, transactionService);
		this.accountService =  accountService;
	}

	public void start() {
		Scanner scanner = new Scanner(System.in);
		System.out.println("\n=== Fintech CLI Menu ===");
		System.out.println("1. Login");
		System.out.println("2. Signup");
		System.out.print("Enter your choice: ");

		String userChoice = scanner.nextLine().trim();
		while (!userChoice.matches("^(1|2)$")) {
			System.out.print("Please enter 1 or 2: ");
			userChoice = scanner.nextLine().trim();
		}
		switch (userChoice) {
		case "1" -> {
			
			String userName = authService.login(scanner);
			if (userName != null) {
				menuService.menu(scanner, userName);

			}
			
			else {
				System.exit(0);
				return;
			}
		}
		case "2" -> {
			String userName = authService.signup(scanner);
			if (userName != null) {
				menuService.menu(scanner, userName);
				Account account  = accountService.findByUserName(userName);
				System.out.println(account.getAccountId());
			}else
				authService.retry(scanner, this::start);
		}
		}
	}
}
