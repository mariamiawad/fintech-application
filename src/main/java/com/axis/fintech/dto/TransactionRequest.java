package com.axis.fintech.dto;

public class TransactionRequest {

	private String userName;
	private Double amount;
	private String password;
	public TransactionRequest() {
	}

	public TransactionRequest(String userName, Double amount) {
		this.userName = userName;
		this.amount = amount;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}
	public String getPassword() {
		return password;
	}
}
