# 🏦 Banking Transactions API

[![Java 21](https://img.shields.io/badge/Java-21-orange.svg?logo=openjdk)](https://www.oracle.com/java/technologies/downloads/#java21)
[![Spring Boot 4.x](https://img.shields.io/badge/Spring%20Boot-4.1.1-brightgreen.svg?logo=springboot)](https://spring.io/projects/spring-boot)
[![Architecture](https://img.shields.io/badge/Architecture-Hexagonal%20%2F%20DDD-blue.svg)](#-hexagonal-architecture--ddd)
[![MySQL](https://img.shields.io/badge/Database-MySQL%208.0-blue.svg?logo=mysql)](https://www.mysql.com/)
[![Docker](https://img.shields.io/badge/Docker-Ready-2496ED.svg?logo=docker)](https://www.docker.com/)
[![Tests](https://img.shields.io/badge/Tests-41%20Passed-success.svg)](#-testing-strategy)

Enterprise-grade, transactional RESTful API tailored for **Fintech & Core Banking** environments. Designed strictly adhering to **Hexagonal Architecture (Ports & Adapters)**, **Domain-Driven Design (DDD)**, and clean code principles (**SOLID**, immutability, rich Value Objects, and defensive programming).

---

## 📑 Table of Contents

- [🎯 Project Purpose](#-project-purpose)
- [🏛️ Hexagonal Architecture & DDD](#️-hexagonal-architecture--ddd)
- [🛠️ Tech Stack](#️-tech-stack)
- [📁 Project Structure](#-project-structure)
- [🚀 Getting Started (Local & Docker)](#-getting-started-local--docker)
- [📡 API Endpoints & cURL Examples](#-api-endpoints--curl-examples)
- [🧪 Testing Strategy](#-testing-strategy)
- [🛡️ Centralized Error Handling](#️-centralized-error-handling)
- [🗺️ Roadmap & Future Enhancements](#️-roadmap--future-enhancements)
- [👨‍💻 Author & License](#-author--license)

---

## 🎯 Project Purpose

The primary objective of this project is to showcase how to architect a **resilient and maintainable banking core**, where critical business rules remain independent from external frameworks, persistence mechanisms, or delivery mechanisms.

### Core Principles:
* **Framework Independence:** The Domain layer is completely decoupled and free from Spring Boot or JPA/Hibernate annotations.
* **Monetary Precision:** Money is encapsulated within a `Money` Value Object built on top of `BigDecimal`, enforcing a fixed 2-decimal scale and `HALF_UP` banking rounding to eliminate floating-point precision flaws.
* **Overdraft Prevention:** An invariant rule prevents negative balances and rejects withdrawals exceeding the available balance (`InsufficientBalanceException`).
* **Auditability & Immutability:** Every deposit and withdrawal appends an immutable, audit-ready `Transaction` record with an automated UTC timestamp.

---

## 🏛️ Hexagonal Architecture & DDD

The application enforces the **Dependency Inversion Principle (DIP)**: all code dependencies flow strictly inwards toward the core domain.

```
       [ INBOUND ADAPTER ]           --> (HTTP REST Controller / JSON)
                │
                ▼
       [ DRIVING PORT ]              --> (CreateAccountUseCase interface)
                │
┌───────────────┼────────────────────────────────────────┐
│               ▼                                        │
│     [ APPLICATION LAYER ]                              │
│       - Use Case Orchestration (Services)              │
│       - Commands (Write Model) & DTOs (Read Model)     │
│               │                                        │
│               ▼                                        │
│     [ DOMAIN LAYER ] (Pure Java Core - Agnostic)       │
│       - Aggregate Root (Account)                       │
│       - Immutable Entities & History (Transaction)     │
│       - Value Objects (Money, AccountId)               │
│       - Business Exceptions                            │
│               │                                        │
│               ▼                                        │
│     [ DRIVEN PORT ]                                    │
│       - Repository Interface (AccountRepository)       │
└───────────────┼────────────────────────────────────────┘
                │
                ▼
       [ OUTBOUND ADAPTER ]          --> (AccountRepositoryAdapter / MySQL JPA)
```

### Layer Responsibilities:
1. **`domain/` (Core):** Pure business logic (`Account`, `Transaction`, `Money`, `AccountId`), driven ports (`AccountRepository`), and domain exceptions (`InsufficientBalanceException`, `AccountNotFoundException`, `NegativeMoneyException`).
2. **`application/` (Orchestration):** Use cases (`CreateAccountUseCase`, `DepositMoneyUseCase`, `WithdrawMoneyUseCase`, `GetAccountDetailsUseCase`), immutable Commands, and DTO mappers.
3. **`infrastructure/` (Technology):** REST controllers (`AccountController`), outbound persistence adapters (`AccountRepositoryAdapter`, `SpringDataAccountRepository`), JPA entities (`AccountEntity`, `TransactionEntity`), and the Global Exception Handler.

---

## 🛠️ Tech Stack

| Component | Technology | Rationale |
|---|---|---|
| **Language** | **Java 21 (LTS)** | Immutable records, pattern matching, switch expressions, modern standard library. |
| **Framework** | **Spring Boot 4.x** | Dependency injection, auto-configuration, and native observability. |
| **Persistence** | **Spring Data JPA / Hibernate** | Relational mapping isolated behind outbound repository adapters. |
| **Database** | **MySQL 8.0** | Full ACID-compliant relational transactional engine. |
| **Testing** | **JUnit 5 + Mockito + MockMvc** | Fast domain unit tests, mocked application use cases, and WebMvc slice tests. |
| **Containers** | **Docker & Docker Compose** | Reproducible, isolated local development and deployment environment. |

---

## 📁 Project Structure

```
src/main/java/com/banking/transactions/
├── TransactionsApplication.java
├── health/
│   └── HealthCheckController.java                 # Health check (/health)
├── domain/                                        # DOMAIN (100% Pure Java)
│   ├── exception/
│   │   ├── AccountNotFoundException.java
│   │   ├── InsufficientBalanceException.java
│   │   └── NegativeMoneyException.java
│   ├── model/
│   │   ├── Account.java                           # Aggregate Root
│   │   ├── AccountId.java                         # Value Object (Record)
│   │   ├── Money.java                             # Value Object (BigDecimal + HALF_UP)
│   │   ├── Transaction.java                       # Immutable Entity
│   │   └── TransactionType.java                   # Enum { DEPOSIT, WITHDRAW }
│   └── port/
│       └── AccountRepository.java                 # Driven Port (Outbound)
├── application/                                   # APPLICATION (Use Cases)
│   ├── dto/
│   │   ├── CreateAccountCommand.java              # Write Model (Command)
│   │   ├── DepositMoneyCommand.java
│   │   ├── WithdrawMoneyCommand.java
│   │   ├── AccountDetailsDto.java                 # Read Model (DTO)
│   │   ├── TransactionDto.java
│   │   └── MapToAccountDetailsDto.java            # Anti-Corruption Layer Mapper
│   ├── port/
│   │   ├── CreateAccountUseCase.java              # Driving Ports (Inbound)
│   │   ├── DepositMoneyUseCase.java
│   │   ├── WithdrawMoneyUseCase.java
│   │   └── GetAccountDetailsUseCase.java
│   └── service/
│       ├── CreateAccountService.java              # Use Case Implementations
│       ├── DepositMoneyService.java
│       ├── WithdrawMoneyService.java
│       └── GetAccountDetailsService.java
└── infrastructure/                                # INFRASTRUCTURE (Adapters)
    ├── adapter/
    │   └── AccountRepositoryAdapter.java          # Outbound Adapter (JPA Adapter)
    ├── mapper/
    │   └── AccountMapper/
    │       └── AccountMapper.java                 # Entity <-> Domain Mapper
    ├── persistence/
    │   ├── AccountEntity.java                     # JPA Entity (accounts)
    │   └── TransactionEntity.java                 # JPA Entity (transactions)
    ├── repository/
    │   └── SpringDataAccountRepository.java       # Spring Data JPA Interface
    └── web/
        ├── controller/
        │   └── AccountController.java             # Inbound Adapter (REST Controller)
        ├── dto/
        │   ├── CreateAccountRequest.java
        │   ├── DepositRequest.java
        │   ├── WithdrawRequest.java
        │   ├── AccountResponse.java
        │   └── ErrorMessage.java                  # Unified API error payload
        └── handler/
            └── GlobalExceptionHandler.java        # Centralized @RestControllerAdvice
```

---

## 🚀 Getting Started (Local & Docker)

### Prerequisites
* **Java 21**
* **Docker & Docker Compose**

### 1. Start MySQL with Docker
Start the MySQL database container:
```bash
docker compose up -d mysql
```

### 2. Run the Application Locally
```bash
./mvnw spring-boot:run
```
The API will be available at `http://localhost:8080`.

### 3. Or Run Everything via Docker Compose
```bash
docker compose up -d --build
```

---

## 📡 API Endpoints & cURL Examples

### 1. Create a Bank Account
`POST /api/accounts`
```bash
curl -X POST http://localhost:8080/api/accounts \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "cust-100",
    "initialBalance": 200.00
  }'
```
**Response (`201 Created`):**
```json
{
  "id": "7ef0ebae-2139-4c16-9244-120309c04d21",
  "customerId": "cust-100",
  "balance": 200.0,
  "transactions": []
}
```

---

### 2. Deposit Money
`POST /api/accounts/{id}/deposit`
```bash
curl -X POST http://localhost:8080/api/accounts/7ef0ebae-2139-4c16-9244-120309c04d21/deposit \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 50.00
  }'
```
**Response (`200 OK`):**
```json
{
  "id": "7ef0ebae-2139-4c16-9244-120309c04d21",
  "customerId": "cust-100",
  "balance": 250.0,
  "transactions": [
    {
      "id": "c1f7b7cb-9c12-4c2d-944a-e490518d89a2",
      "transactionType": "DEPOSIT",
      "amount": 50.0
    }
  ]
}
```

---

### 3. Withdraw Money
`POST /api/accounts/{id}/withdraw`
```bash
curl -X POST http://localhost:8080/api/accounts/7ef0ebae-2139-4c16-9244-120309c04d21/withdraw \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 100.00
  }'
```
**Response (`200 OK`):**
```json
{
  "id": "7ef0ebae-2139-4c16-9244-120309c04d21",
  "customerId": "cust-100",
  "balance": 150.0,
  "transactions": [
    {
      "id": "c1f7b7cb-9c12-4c2d-944a-e490518d89a2",
      "transactionType": "DEPOSIT",
      "amount": 50.0
    },
    {
      "id": "f5e921d3-34e8-4680-bc42-ff832d203a11",
      "transactionType": "WITHDRAW",
      "amount": 100.0
    }
  ]
}
```

---

### 4. Get Account Details
`GET /api/accounts/{id}`
```bash
curl -X GET http://localhost:8080/api/accounts/7ef0ebae-2139-4c16-9244-120309c04d21
```

---

### 5. Health Check
`GET /health`
```bash
curl -X GET http://localhost:8080/health
```
**Response:** `{"status": 200}`

---

## 🧪 Testing Strategy

The project implements a comprehensive testing pyramid:

```
          / \
         /   \       Integration Tests (WebMvc MockMvc + ContextLoads)
        /-----\      
       /       \     Application Service Tests (Mockito Unit Tests)
      /---------\    
     /           \   Domain & VO Unit Tests (Pure Java - Fast & Isolated)
    /-------------\  
```

### Run All Tests (41 tests, 0 failures):
```bash
./mvnw test
```
Or run the suite directly from your IDE:
* **[`AllTestsSuite.java`](src/test/java/com/banking/transactions/AllTestsSuite.java)** (`@Suite` from JUnit 5).

### Test Breakdown:
* **Pure Domain (`MoneyTest`, `TransactionTest`, `AccountTest`):** 19 tests validating business invariants, rounding, arithmetic immutability, negative balance protection, and unmodifiable lists.
* **Application Services (`CreateAccountServiceTest`, `DepositMoneyServiceTest`, `WithdrawMoneyServiceTest`, `GetAccountDetailsServiceTest`):** 9 Mockito tests verifying use case orchestration, state preservation, and exception propagation.
* **Mappers (`AccountMapperTest`):** 2 unit tests covering bidirectional `Account` $\leftrightarrow$ `AccountEntity` transformations.
* **Web Integration (`AccountControllerTest`):** 6 tests with `MockMvc` validating HTTP status codes (`200`, `201`, `400`, `404`, `422`), request payload validation, and `ErrorMessage` response structures.
* **Context & Smoke (`TransactionsApplicationTests`):** Full Spring context and database configuration verification.

---

## 🛡️ Centralized Error Handling

Domain exceptions are mapped into standard HTTP responses within [`GlobalExceptionHandler.java`](src/main/java/com/banking/transactions/infrastructure/web/handler/GlobalExceptionHandler.java), keeping controllers clean from `try-catch` blocks:

| Exception | HTTP Status | Description |
|---|---|---|
| `AccountNotFoundException` | `404 Not Found` | Requested account does not exist. |
| `InsufficientBalanceException` | `422 Unprocessable Content` | Account lacks sufficient funds for withdrawal. |
| `NegativeMoneyException` | `400 Bad Request` | Monetary value is less than or equal to zero. |
| `MethodArgumentNotValidException` | `400 Bad Request` | Payload failed `@Valid` validation constraints. |
| `IllegalArgumentException` | `400 Bad Request` | Malformed inputs (e.g., invalid UUID). |

### Standard Error Response (`ErrorMessage`):
```json
{
  "status": 422,
  "error": "Unprocessable Entity",
  "message": "Insufficient balance",
  "path": "/api/accounts/7ef0ebae-2139-4c16-9244-120309c04d21/withdraw",
  "timestamp": "2026-09-25T12:31:55.052Z",
  "details": []
}
```

---

## 🗺️ Roadmap & Future Enhancements

- [ ] **Inter-Account Transfers:** Add a `TransferMoneyUseCase` ensuring atomic transfers between two accounts (ACID-compliant).
- [ ] **Banking Security:** Integrate Spring Security with JWT-based authentication and role authorization.
- [ ] **Transaction Types as Entities/Catalog:** Evolve `TransactionType` into a dynamic database-driven entity to support configurable fees, daily limits, and ISO 20022 purpose codes.
- [ ] **Testcontainers Integration:** Spin up ephemeral MySQL test containers for seamless CI/CD test execution.
- [ ] **OpenAPI / Swagger Documentation:** Interactive API documentation via Springdoc OpenAPI.

---

## 👨‍💻 Author & License

Developed as an enterprise-grade software engineering reference project focusing on Clean Architecture and Domain-Driven Design in banking.

Licensed under the **MIT License**.
