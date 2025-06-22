# 🏦 Fintech Application

This is a Spring Boot CLI/web application simulating core fintech operations such as:

- User authentication (Sign up / Login) using JWT
- Secure deposit and withdrawal
- Checking account balance
- File-based data persistence
- REST APIs with Swagger documentation

---

## 📦 Features

- ✅ **User registration and login**
- 🔐 **JWT-based authentication**
- 💸 **Secure transactions** (deposit, withdraw, check balance)
- 📁 **JSON-based storage** for accounts and transactions
- 🌐 **Swagger UI** for exploring APIs
- 🧪 **JUnit tests** for controllers and services
- 🐳 **Docker support** for deployment

---

## 🚀 Getting Started

### 1. Clone and Build

```bash
git clone https://github.com/your-repo/fintech-application.git
cd fintech-application
./mvnw clean package -DskipTests
```

### 2. Run Locally

```bash
java -jar target/fintech-application.jar
```

The application will start on [http://localhost:8080](http://localhost:8080)

---

## 🔐 Authentication

Authentication is handled using JWT. Upon successful login, a JWT is returned in the `Authorization` header. Include this token in subsequent requests:

```
Authorization: Bearer <your-jwt-token>
```

---

## 🧪 Running Tests

```bash
./mvnw test
```

Includes tests for:

- `AuthController`
- `ProtectedTransactionController`
- `AccountService`
- `TransactionService`

---

## 🐳 Docker Support

### Build Docker Image

```bash
docker build -t fintech-app .
```

### Run Container

```bash
docker run -p 8080:8080 fintech-app
```

---

## 🧾 API Endpoints

### Public

| Method | Endpoint            | Description        |
|--------|---------------------|--------------------|
| POST   | `/api/auth/signup`  | Register a user    |
| POST   | `/api/auth/login`   | Login and get JWT  |

### Secure (Require JWT)

| Method | Endpoint               | Description             |
|--------|------------------------|-------------------------|
| POST   | `/api/secure/deposit`  | Deposit funds           |
| POST   | `/api/secure/withdraw` | Withdraw funds          |
| POST   | `/api/secure/balance`  | Check account balance   |

---

## 📚 Swagger UI

Access Swagger documentation at:

```
http://localhost:8080/swagger-ui/index.html
```

---

## 🗂 Project Structure

```
├── controller
│   └── AuthController.java
│   └── ProtectedTransactionController.java
├── service
│   └── AccountService.java
│   └── TransactionService.java
├── model
│   └── Account.java
│   └── Transaction.java
├── security
│   └── JwtTokenProvider.java
│   └── JwtAuthenticationFilter.java
├── dto
│   └── LoginRequest.java
│   └── SignupRequest.java
│   └── TransactionRequest.java
├── utils
│   └── HashUtil.java
│   └── JsonFileHandler.java
└── fintech-application.jar
```

---

## 🧠 Credits

Developed with ❤️ by [Mariam Awad]
