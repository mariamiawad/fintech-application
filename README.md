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
- 🐳 Dockerized for containerized CLI execution

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
- Maven

### Build & Run

```bash
# Compile the JAR
./mvnw clean package

# Run the application
java -jar target/fintech-cli-0.0.1-SNAPSHOT.jar
```

---

## 🐳 Docker Support

### Build Docker Image

```bash
docker build -t fintech-cli .
```

### Run the Application

```bash
docker run -it --rm fintech-cli
```

### Persist JSON Data

```bash
docker run -it --rm -v $(pwd)/data:/app/data fintech-cli
```

Ensure your `application.properties` or `class.properties` contains:

```properties
accounts.path=/app/data/accounts.json
transactions.path=/app/data/transactions.json
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

---

## 🧪 Running Tests

```bash
./mvnw test
```

Test coverage includes:

- `AccountServiceTest`
- `TransactionServiceTest`
- `MenuServiceTest`
- `JsonFileHandlerTest`
- `IdGeneratorTest`
- `HashUtilTest`

---

## 🔒 Security

- Passwords are hashed using SHA-256
- Regex validation for usernames
- No plain-text password storage

---

## 📌 Future Work

- Add REST API interface
- Add transaction filtering
- Role-based access control
- User analytics and auditing

---

## 👤 Author

Built by **Mariam Awad**

---

## 📄 License

This project is licensed under the [MIT License](LICENSE).
