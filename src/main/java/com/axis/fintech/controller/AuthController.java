package com.axis.fintech.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.axis.fintech.dto.LoginRequest;
import com.axis.fintech.dto.SignupRequest;
import com.axis.fintech.dto.UserChoiceRequest;
import com.axis.fintech.model.Account;
import com.axis.fintech.security.JwtTokenProvider;
import com.axis.fintech.service.AccountService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthenticationManager authenticationManager;
	private final JwtTokenProvider jwtTokenProvider;
	private final AccountService accountService;

	public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider,
			AccountService accountService) {
		this.authenticationManager = authenticationManager;
		this.jwtTokenProvider = jwtTokenProvider;
		this.accountService = accountService;
	}

	@Operation(summary = "Login", description = "Authenticates a user and returns a success message if credentials are valid.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Login successful"),
			@ApiResponse(responseCode = "401", description = "Invalid password"),
			@ApiResponse(responseCode = "404", description = "Account not found") })
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest request) {
		Account account = accountService.findByUserName(request.getUserName());

		if (account == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found: " + request.getUserName());
		}

		boolean valid = accountService.validatePassword(request.getUserName(), request.getPassword());
		if (!valid) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password");
		}

		UserDetails userDetails = User.builder().username(account.getUserName()).password(account.getPasswordHash())
				.roles("USER").build();

		String jwt = jwtTokenProvider.generateToken(userDetails);
		return ResponseEntity.status(HttpStatus.OK).header("Authorization", "Bearer " + jwt).body("Login successful");
	}

	@Operation(summary = "Signup", description = "Registers a new user and returns the newly created Account ID.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Account successfully created"),
			@ApiResponse(responseCode = "400", description = "Invalid input or user already exists") })
	@PostMapping("/signup")
	public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
		if (accountService.findByUserName(request.getUserName()) != null) {
	        return ResponseEntity
	                .badRequest()
	                .body("Username already exists");
	    }
		String username = request.getUserName();
        String regex = "^[a-zA-Z]+\\d*$";
        if (!username.matches(regex)) {
            return ResponseEntity.badRequest().body("Invalid username format");
        }		
		Long accountId = accountService.openAccount(request.getUserName(), request.getPassword());
		return ResponseEntity.ok("Account created successfully. Your account ID is: " + accountId);
	}

	@Operation(summary = "Handle user choice", description = "Redirects to a specific endpoint based on the user's menu choice.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "302", description = "Redirecting to selected action", content = @Content),
			@ApiResponse(responseCode = "400", description = "Invalid choice", content = @Content) })
	@PostMapping("/choice")
	public ResponseEntity<?> handleChoice(@RequestBody UserChoiceRequest choiceRequest) {
		int choice = choiceRequest.getChoice();
		String target = switch (choice) {
		case 1 -> "/api/secure/deposit";
		case 2 -> "/api/secure/withdraw";
		case 3 -> "/api/secure/balance";
		default -> null;
		};

		if (target == null)
			return ResponseEntity.badRequest().body("Invalid choice");

		return ResponseEntity.status(302).header("Location", target).body("Redirecting to: " + target);
	}
}
