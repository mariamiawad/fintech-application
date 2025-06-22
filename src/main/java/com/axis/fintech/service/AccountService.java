package com.axis.fintech.service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.axis.fintech.model.Account;
import com.axis.fintech.utils.HashUtil;
import com.axis.fintech.utils.IdGenerator;
import com.axis.fintech.utils.JsonFileHandler;

import jakarta.annotation.PostConstruct;

@Service
public class AccountService {
	
	private List<Account> accounts = null;

	@PostConstruct
	public void init() {
		try {
			this.accounts = JsonFileHandler.loadAccounts();
		} catch (Exception e) {
			this.accounts = new ArrayList<>();
			System.err.println("Failed to load accounts: " + e.getMessage());
		}
	}

	public Long openAccount(String userName, String plainPassword) {
		boolean exists = accounts.stream().anyMatch(a -> a.getUserName().equalsIgnoreCase(userName));
		if (exists) {
			System.err.println("Can not open account with this user name");
			System.err.println("user name exists");
			return null;
		}
        String regex = "^[a-zA-Z]+\\d*$";
        Pattern pattern = Pattern.compile(regex);
        if (!pattern.matcher(userName).matches()) {
            System.err.println("Invalid username: " + userName + " (must start with letters and may end with digits)");
            return null;
        }
		Long accountId = IdGenerator.nextAccountId();
	    String hashedPassword = HashUtil.sha256(plainPassword);

		Account account = new Account(userName, accountId, 0.0, hashedPassword);
		accounts.add(account);
		JsonFileHandler.saveAccounts(accounts);
		return accountId;
	}

	public Account findByUserName(String userName) {
		return accounts.stream().filter(a -> a.getUserName().equalsIgnoreCase(userName)).findFirst().orElse(null);
	}

	public Double getBalance(String userName) {
		Account account = findByUserName(userName);
		if (account == null) {
			System.err.println("user doesn't exists please open an account first");
			return null;
		}
		double balance = account.getBalance();
		
		return balance;

	}

	public void updateAccount(Account account) {
		JsonFileHandler.saveAccounts(accounts);
	}

	public List<Account> getAllAccounts() {
		return accounts;
	}
	public boolean validatePassword(String userName, String inputPassword) {
        Account account = findByUserName(userName);
        if (account == null) return false;

        String hashedInput = HashUtil.sha256(inputPassword);
        return hashedInput.equals(account.getPasswordHash());
    }
}
