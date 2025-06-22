package com.axis.fintech.controller;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

	@GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
	@ResponseBody
	public String homePage() {
		return """
								<!DOCTYPE html>
				<html lang="en">
				<head>
				  <meta charset="UTF-8">
				  <title>Fintech Home</title>
				  <style>
				    .hidden { display: none; }
				    body { font-family: Arial; margin: 30px; }
				    .form-container { margin-top: 20px; }
				  </style>
				</head>
				<body>
				  <h1>🏦 Fintech Home</h1>

				  <button onclick="showForm('login')">Sign In</button>
				  <button onclick="showForm('signup')">Sign Up</button>

				  <!-- Sign In -->
				  <div id="login-form" class="form-container hidden">
				    <h2>Sign In</h2>
				    <form onsubmit="login(event)">
				      <input type="text" id="login-username" placeholder="Username" required><br><br>
				      <input type="password" id="login-password" placeholder="Password" required><br><br>
				      <button type="submit">Login</button>
				    </form>
				  </div>

				  <!-- Sign Up -->
				  <div id="signup-form" class="form-container hidden">
				    <h2>Sign Up</h2>
				    <form onsubmit="signup(event)">
				      <input type="text" id="signup-username" placeholder="Username" required><br><br>
				      <input type="password" id="signup-password" placeholder="Password" required><br><br>
				      <button type="submit">Register</button>
				    </form>
				  </div>

				  <!-- Choice -->
				  <div id="choice-form" class="form-container hidden">
				    <h2>Enter Your Choice</h2>
				    <p>1: Deposit | 2: Withdraw | 3: Check Balance</p>
				    <form onsubmit="submitChoice(event)">
				      <input type="number" id="choice" min="1" max="3" required><br><br>
				      <button type="submit">Continue</button>
				    </form>
				  </div>

				  <!-- Deposit -->
				  <div id="deposit-form" class="form-container hidden">
				    <h2>Deposit Funds</h2>
				    <form onsubmit="submitTransaction(event, 'deposit')">
				      <input type="number" id="deposit-amount" placeholder="Amount" required><br><br>
				      <button type="submit">Deposit</button>
				    </form>
				  </div>

				  <!-- Withdraw -->
				  <div id="withdraw-form" class="form-container hidden">
				    <h2>Withdraw Funds</h2>
				    <form onsubmit="submitTransaction(event, 'withdraw')">
				      <input type="number" id="withdraw-amount" placeholder="Amount" required><br><br>
				      <button type="submit">Withdraw</button>
				    </form>
				  </div>

				  <!-- Balance -->
				  <div id="balance-result" class="form-container hidden">
				    <h2>Balance</h2>
				    <p id="balance-output"></p>
				  </div>

				  <script>
				    let jwtToken = '';
				    let loggedInUser = '';

				    function showForm(formType) {
				      ['login', 'signup', 'choice', 'deposit', 'withdraw', 'balance-result'].forEach(f =>
				        document.getElementById(f + '-form')?.classList.add('hidden')
				      );
				      document.getElementById(formType + '-form')?.classList.remove('hidden');
				    }

				    function login(event) {
				      event.preventDefault();
				      const userName = document.getElementById('login-username').value;
				      const password = document.getElementById('login-password').value;

				      fetch('/api/auth/login', {
				        method: 'POST',
				        headers: { 'Content-Type': 'application/json' },
				        body: JSON.stringify({ userName, password })
				      })
				      .then(async res => {
				const msg = await res.text();

				if (!res.ok) {
				  // Provide clear feedback based on the backend response
				  throw new Error(msg);
				}
				        jwtToken = res.headers.get('Authorization');
				        loggedInUser = userName;
				        return msg;
				      })
				      .then(msg => {
				        alert(msg);
				        showForm('choice');
				      })
				      .catch(err => alert(err.message));
				    }

				    function signup(event) {
				      event.preventDefault();
				      const userName = document.getElementById('signup-username').value;
				      const password = document.getElementById('signup-password').value;

				      fetch('/api/auth/signup', {
				        method: 'POST',
				        headers: { 'Content-Type': 'application/json' },
				        body: JSON.stringify({ userName, password })
				      })
				      .then(res => res.text())
				      .then(msg => alert(msg))
				      .catch(err => alert('Signup failed: ' + err.message));
				    }

				    function submitChoice(event) {
				      event.preventDefault();
				      const choice = parseInt(document.getElementById('choice').value);
				      switch (choice) {
				        case 1:
				          showForm('deposit');
				          break;
				        case 2:
				          showForm('withdraw');
				          break;
				        case 3:
				          checkBalance();
				          break;
				        default:
				          alert("Invalid choice");
				      }
				    }

				    function submitTransaction(event, type) {
				      event.preventDefault();
				      const amount = parseFloat(document.getElementById(`${type}-amount`).value);

				      fetch(`/api/secure/${type}`, {
				        method: 'POST',
				        headers: {
				          'Content-Type': 'application/json',
				          'Authorization': jwtToken
				        },
				        body: JSON.stringify({ userName: loggedInUser, amount })
				      })
				      .then(res => res.text())
				      .then(msg => alert(msg))
				      .catch(err => alert(err.message));
				    }

				    function checkBalance() {
				      fetch("/api/secure/balance", {
				        method: 'POST',
				        headers: {
				          'Content-Type': 'application/json',
				          'Authorization': jwtToken
				        },
				        body: JSON.stringify({ userName: loggedInUser })
				      })
					.then(res => res.text())
				      .then(msg => alert(msg))				      .then(data => {
				        document.getElementById('balance-output').innerText = data;
				        showForm('balance-result');
				      })
				      .catch(err => alert("Error: " + err.message));
				    }
				  </script>
				</body>
				</html>

												       """;
	}
}
