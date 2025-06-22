package com.axis.fintech.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.axis.fintech.dto.TransactionRequest;
import com.axis.fintech.model.Account;
import com.axis.fintech.service.AccountService;
import com.axis.fintech.service.TransactionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/secure")
public class ProtectedTransactionController {

	private final AccountService accountService;
	private final TransactionService transactionService;

	public ProtectedTransactionController(AccountService accountService, TransactionService transactionService) {
		this.accountService = accountService;
		this.transactionService = transactionService;
	}

	@Operation(summary = "Deposit funds securely", description = "Requires valid username and password. Redirects to home if authentication fails.")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Transaction successful"),
			@ApiResponse(responseCode = "302", description = "Redirect to home due to invalid user or password") })
	@PostMapping("/deposit")
	public ResponseEntity<?> securedDeposit(@RequestBody TransactionRequest request) {
	    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	    String username = (principal instanceof UserDetails)
	        ? ((UserDetails) principal).getUsername()
	        : principal.toString();

	    Account account = accountService.findByUserName(username);
	    if (account == null) {
	        return ResponseEntity.status(302).header("Location", "/")
	            .body("User not found. Redirecting to home...");
	    }

	    Long txId = transactionService.deposit(username, request.getAmount());
	    if(txId == null) {
	    	return ResponseEntity.status(302).header("Location", "/")
		            .body("User not found. Redirecting to home...");
		    
	    }
	    return ResponseEntity.ok("Transaction successful. ID: " + txId);
	}

	@Operation(summary = "Withdraw funds securely", description = "Requires valid username and password. Redirects to home if authentication fails.")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Transaction successful"),
			@ApiResponse(responseCode = "302", description = "Redirect to home due to invalid user or password") })
	@PostMapping("/withdraw")
	public ResponseEntity<?> securedWithdraw(@RequestBody TransactionRequest request) {
	    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	    String username = (principal instanceof UserDetails)
	        ? ((UserDetails) principal).getUsername()
	        : principal.toString();

	    Account account = accountService.findByUserName(username);
	    if (account == null) {
	        return ResponseEntity.status(302).header("Location", "/")
	            .body("User not found. Redirecting to home...");
	    }

	    Long txId = transactionService.withdraw(username, request.getAmount());
	    if(txId == null) {
	    	return ResponseEntity.status(200).header("Location", "/")
		            .body("You don't have enough balance");
		    
	    }
	    return ResponseEntity.ok("Transaction successful. ID: " + txId);
	}
	@Operation(summary = "Secure balance check", description = "Retrieves balance if user credentials are valid")

	@PostMapping("/balance")
	public ResponseEntity<?> secureBalance(@RequestBody TransactionRequest request) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	    String username = (principal instanceof UserDetails)
	        ? ((UserDetails) principal).getUsername()
	        : principal.toString();

	    Account account = accountService.findByUserName(username);
	    if (account == null) {
	        return ResponseEntity.status(302).header("Location", "/")
	            .body("User not found. Redirecting to home...");
	    }

		    Double balance = transactionService.checkBalance(username);
		    return ResponseEntity.ok("Your balance is: " + balance);
	}

}
