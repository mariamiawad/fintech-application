# 💸 Fintech CLI Application

A Java-based CLI banking application for managing accounts, secure login, deposits, and withdrawals with JSON persistence.

---

## 📦 Features

- 🔐 Secure account signup and login
- 💰 Deposit and withdraw funds
- 📊 View account balances
- 🗂️ JSON-based persistent storage
- 🧪 Full unit test coverage with JUnit 5 & Mockito
- 🛡️ Password hashing via SHA-256

---

## 🧱 Project Structure

```
src/
├── main/
│   ├── java/com/axis/fintech/
│   │   ├── cli/              # CLI entry points: MenuService, AuthService
│   │   ├── model/            # Account and Transaction models
│   │   ├── service/          # Business logic: AccountService, TransactionService
│   │   ├── utils/            # Hashing, ID generation, JSON handlers
│   │   └── repository/       # Optional: account data repo
│   └── resources/
│       └── class.properties  # Path configs for JSON files
└── test/
    └── (Unit tests for all modules)
```

---

## 🛠️ Setup

### Requirements

- Java 17+
- Maven or Gradle

### Build & Run

```bash
# Compile and run the application
mvn clean install
mvn spring-boot:run
```

---

## 📁 JSON File Format

### `accounts.json`

```json
{
  "accounts": [
    {
      "accountId": 1000,
      "userName": "username",
      "balance": 40.0,
      "timestamp": "2025-06-20T15:55:07.019",
      "passwordHash": "hash",
      "transactions": [1, 2]
    }
  ]
}
```

### `transactions.json`

```json
{
  "transactions": [
    {
      "transactionId": 1,
      "accountId": 1000,
      "amount": 20.0,
      "type": "DEPOSIT",
      "timestamp": "2025-06-20T16:00:00"
    }
  ]
}
```

### `class.properties`

```properties
accounts.path=src/main/resources/accounts.json
transactions.path=src/main/resources/transactions.json
```

---

## 🧪 Running Tests

```bash
mvn test
```

Tests include:

- `AccountServiceTest`
- `TransactionServiceTest`
- `MenuServiceTest`
- `JsonFileHandlerTest`
- `IdGeneratorTest`
- `HashUtilTest`

---

## 🔒 Security

- Passwords are hashed with SHA-256
- Usernames validated using regex
- No passwords stored in plain text

---

## 📌 Future Work

- Add REST API layer
- Add transaction history filters
- Add Docker support
- Add role-based admin controls

---

## 👤 Author

Built by **Mariam Awad**

---

## 📄 License

This project is licensed under the [MIT License](LICENSE).
