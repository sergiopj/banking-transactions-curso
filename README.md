# 🏦 Banking Transactions API

[![Java 21](https://img.shields.io/badge/Java-21-orange.svg?logo=openjdk)](https://www.oracle.com/java/technologies/downloads/#java21)
[![Spring Boot 4.x](https://img.shields.io/badge/Spring%20Boot-4.1.1-brightgreen.svg?logo=springboot)](https://spring.io/projects/spring-boot)
[![Architecture](https://img.shields.io/badge/Architecture-Hexagonal%20%2F%20DDD-blue.svg)](#-arquitectura-hexagonal-y-ddd)
[![MySQL](https://img.shields.io/badge/Database-MySQL%208.0-blue.svg?logo=mysql)](https://www.mysql.com/)
[![Docker](https://img.shields.io/badge/Docker-Ready-2496ED.svg?logo=docker)](https://www.docker.com/)
[![Tests](https://img.shields.io/badge/Tests-41%20Passed-success.svg)](#-estrategia-de-testing)

API RESTful transaccional de grado empresarial orientada al sector **Fintech / Banking Core**. Diseñada siguiendo estrictamente **Arquitectura Hexagonal (Ports & Adapters)**, **Domain-Driven Design (DDD)** y buenas prácticas de código limpio (**SOLID**, inmutabilidad, Value Objects y defensive programming).

---

## 📑 Tabla de Contenidos

- [🎯 Propósito del Proyecto](#-propósito-del-proyecto)
- [🏛️ Arquitectura Hexagonal y DDD](#️-arquitectura-hexagonal-y-ddd)
- [🛠️ Stack Tecnológico](#️-stack-tecnológico)
- [📁 Estructura del Proyecto](#-estructura-del-proyecto)
- [🚀 Puesta en Marcha (Local & Docker)](#-puesta-en-marcha-local--docker)
- [📡 API Endpoints & Ejemplos cURL](#-api-endpoints--ejemplos-curl)
- [🧪 Estrategia de Testing](#-estrategia-de-testing)
- [🛡️ Manejo Centralizado de Errores](#️-manejo-centralizado-de-errores)
- [🗺️ Roadmap & Siguientes Pasos](#️-roadmap--siguientes-pasos)

---

## 🎯 Propósito del Proyecto

El objetivo de este proyecto es demostrar cómo construir un **core transaccional bancario robusto**, donde el código de negocio sobrevive al paso del tiempo y a cambios tecnológicos. 

### Principios Fundamentales:
* **Independencia Tecnológica:** El Dominio no contiene ninguna anotación de Spring Boot ni dependencias de JPA/Hibernate.
* **Precisión Monetaria:** El dinero se encapsula en un Value Object `Money` usando `BigDecimal` con escala forzada a 2 decimales y redondeo bancario `HALF_UP`, erradicando los errores de coma flotante de tipos primitivos como `double` o `float`.
* **Protección contra Descubiertos:** Invariante de negocio infranqueable que impide saldos negativos o retiros mayores al balance disponible (`InsufficientBalanceException`).
* **Auditoría e Inmutabilidad:** Cada depósito o retiro genera una `Transaction` append-only inmutable con timestamp UTC automático, garantizando trazabilidad completa.

---

## 🏛️ Arquitectura Hexagonal y DDD

La aplicación sigue el **Principio de Inversión de Dependencias (DIP)**: todas las dependencias apuntan hacia el interior.

```
       [ ADAPTADOR ENTRADA ]         --> (HTTP REST Controller / JSON)
                │
                ▼
       [ PUERTO ENTRADA ]            --> (CreateAccountUseCase interface)
                │
┌───────────────┼────────────────────────────────────────┐
│               ▼                                        │
│     [ CAPA DE APLICACIÓN ]                             │
│       - Orquestación de Casos de Uso (Services)        │
│       - Commands (Write Model) & DTOs (Read Model)     │
│               │                                        │
│               ▼                                        │
│     [ CAPA DE DOMINIO ] (Core Agnóstico - Java Puro)   │
│       - Aggregate Root (Account)                       │
│       - Entidades e Histórico (Transaction)            │
│       - Value Objects (Money, AccountId)               │
│       - Excepciones de negocio                         │
│               │                                        │
│               ▼                                        │
│     [ PUERTO DE SALIDA ]                               │
│       - Interfaz (AccountRepository)                   │
└───────────────┼────────────────────────────────────────┘
                │
                ▼
       [ ADAPTADOR SALIDA ]          --> (AccountRepositoryAdapter / MySQL JPA)
```

### Separación de Responsabilidades:
1. **`domain/` (Core):** Modelos puros (`Account`, `Transaction`, `Money`, `AccountId`), contratos de salida (`AccountRepository`) y excepciones de negocio (`InsufficientBalanceException`, `AccountNotFoundException`, `NegativeMoneyException`).
2. **`application/` (Orquestación):** Casos de uso (`CreateAccountUseCase`, `DepositMoneyUseCase`, `WithdrawMoneyUseCase`, `GetAccountDetailsUseCase`), Commands inmutables y mappers a DTOs de salida.
3. **`infrastructure/` (Tecnología):** Adaptadores Web REST (`AccountController`), Adaptadores JPA (`AccountRepositoryAdapter`, `SpringDataAccountRepository`), entidades relacionales (`AccountEntity`, `TransactionEntity`) y Manejador Global de Errores.

---

## 🛠️ Stack Tecnológico

| Componente | Tecnología | Razón de Elección |
|---|---|---|
| **Lenguaje** | **Java 21 (LTS)** | Records inmutables, pattern matching, switch expressions, APIs modernas. |
| **Framework** | **Spring Boot 4.x** | Configuración simplificada, inyección de dependencias y observabilidad nativa. |
| **Persistencia** | **Spring Data JPA / Hibernate** | Mapeo relacional desacoplado del dominio mediante adaptadores. |
| **Base de Datos** | **MySQL 8.0** | Motor transaccional compatible ACID. |
| **Testing** | **JUnit 5 + Mockito + MockMvc** | Testing unitario puro e integración web de alto rendimiento. |
| **Contenedores** | **Docker & Docker Compose** | Entornos portables, reproducibles y aislados. |

---

## 📁 Estructura del Proyecto

```
src/main/java/com/banking/transactions/
├── TransactionsApplication.java
├── health/
│   └── HealthCheckController.java                 # Health check (/health)
├── domain/                                        # DOMINIO (100% Java Puro)
│   ├── exception/
│   │   ├── AccountNotFoundException.java
│   │   ├── InsufficientBalanceException.java
│   │   └── NegativeMoneyException.java
│   ├── model/
│   │   ├── Account.java                           # Aggregate Root
│   │   ├── AccountId.java                         # Value Object (Record)
│   │   ├── Money.java                             # Value Object (BigDecimal + HALF_UP)
│   │   ├── Transaction.java                       # Entity inmutable
│   │   └── TransactionType.java                   # Enum { DEPOSIT, WITHDRAW }
│   └── port/
│       └── AccountRepository.java                 # Puerto de Salida (Driven Port)
├── application/                                   # APLICACIÓN (Casos de Uso)
│   ├── dto/
│   │   ├── CreateAccountCommand.java              # Write Model (Command)
│   │   ├── DepositMoneyCommand.java
│   │   ├── WithdrawMoneyCommand.java
│   │   ├── AccountDetailsDto.java                 # Read Model (DTO)
│   │   ├── TransactionDto.java
│   │   └── MapToAccountDetailsDto.java            # Anti-Corruption Layer Mapper
│   ├── port/
│   │   ├── CreateAccountUseCase.java              # Puertos de Entrada (Driving Ports)
│   │   ├── DepositMoneyUseCase.java
│   │   ├── WithdrawMoneyUseCase.java
│   │   └── GetAccountDetailsUseCase.java
│   └── service/
│       ├── CreateAccountService.java              # Implementaciones de Casos de Uso
│       ├── DepositMoneyService.java
│       ├── WithdrawMoneyService.java
│       └── GetAccountDetailsService.java
└── infrastructure/                                # INFRAESTRUCTURA (Adaptadores)
    ├── adapter/
    │   └── AccountRepositoryAdapter.java          # Adaptador Outbound (JPA Adapter)
    ├── mapper/
    │   └── AccountMapper/
    │       └── AccountMapper.java                 # Mapeo Entity <-> Domain
    ├── persistence/
    │   ├── AccountEntity.java                     # JPA Entity (accounts)
    │   └── TransactionEntity.java                 # JPA Entity (transactions)
    ├── repository/
    │   └── SpringDataAccountRepository.java       # Spring Data JPA
    └── web/
        ├── controller/
        │   └── AccountController.java             # Adaptador Inbound (REST Controller)
        ├── dto/
        │   ├── CreateAccountRequest.java
        │   ├── DepositRequest.java
        │   ├── WithdrawRequest.java
        │   ├── AccountResponse.java
        │   └── ErrorMessage.java                  # Formato unificado de error
        └── handler/
            └── GlobalExceptionHandler.java        # @RestControllerAdvice centralizado
```

---

## 🚀 Puesta en Marcha (Local & Docker)

### Prerrequisitos
* **Java 21**
* **Docker & Docker Compose**

### 1. Iniciar Base de Datos con Docker
Inicia el contenedor de MySQL:
```bash
docker compose up -d mysql
```

### 2. Arrancar la Aplicación (Localmente)
```bash
./mvnw spring-boot:run
```
La API estará disponible en `http://localhost:8080`.

### 3. O Desplegar Todo con Docker Compose
```bash
docker compose up -d --build
```

---

## 📡 API Endpoints & Ejemplos cURL

### 1. Crear una Cuenta Bancaria
`POST /api/accounts`
```bash
curl -X POST http://localhost:8080/api/accounts \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "cust-100",
    "initialBalance": 200.00
  }'
```
**Respuesta (`201 Created`):**
```json
{
  "id": "7ef0ebae-2139-4c16-9244-120309c04d21",
  "customerId": "cust-100",
  "balance": 200.0,
  "transactions": []
}
```

---

### 2. Ingresar Dinero (Depósito)
`POST /api/accounts/{id}/deposit`
```bash
curl -X POST http://localhost:8080/api/accounts/7ef0ebae-2139-4c16-9244-120309c04d21/deposit \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 50.00
  }'
```
**Respuesta (`200 OK`):**
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

### 3. Retirar Dinero
`POST /api/accounts/{id}/withdraw`
```bash
curl -X POST http://localhost:8080/api/accounts/7ef0ebae-2139-4c16-9244-120309c04d21/withdraw \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 100.00
  }'
```
**Respuesta (`200 OK`):**
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

### 4. Consultar Detalle de la Cuenta
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
**Respuesta:** `{"status": 200}`

---

## 🧪 Estrategia de Testing

El proyecto cuenta con una cobertura integral organizada en la pirámide de pruebas:

```
          / \
         /   \       Integration Tests (WebMvc MockMvc + ContextLoads)
        /-----\      
       /       \     Application Service Tests (Mockito Unit Tests)
      /---------\    
     /           \   Domain & VO Unit Tests (Pure Java - Fast & Isolated)
    /-------------\  
```

### Ejecutar todos los tests (41 tests, 0 failures):
```bash
./mvnw test
```
O ejecutando la Test Suite directamente desde el IDE:
* **[`AllTestsSuite.java`](src/test/java/com/banking/transactions/AllTestsSuite.java)** (`@Suite` de JUnit 5).

### Resumen de la Suite de Pruebas:
* **Dominio Puro (`MoneyTest`, `TransactionTest`, `AccountTest`):** 19 tests que validan invariantes, cálculos, límites monetarios, protección contra saldos negativos y listas inmutables.
* **Capa de Aplicación (`CreateAccountServiceTest`, `DepositMoneyServiceTest`, `WithdrawMoneyServiceTest`, `GetAccountDetailsServiceTest`):** 9 tests con Mockito comprobando orquestación, mapeos y propagación de errores.
* **Mappers (`AccountMapperTest`):** 2 tests de conversión bidireccional Dominio $\leftrightarrow$ Entidad JPA.
* **Capa Web e Integración (`AccountControllerTest`):** 6 tests con `MockMvc` validando serialización JSON, códigos `200`, `201`, `400`, `404`, `422` y la estructura de los payloads de error.
* **Context & Smoke (`TransactionsApplicationTests`):** Valida la carga completa del contexto de Spring y el esquema de base de datos.

---

## 🛡️ Manejo Centralizado de Errores

Las excepciones de dominio se interceptan en [`GlobalExceptionHandler.java`](src/main/java/com/banking/transactions/infrastructure/web/handler/GlobalExceptionHandler.java) sin ensuciar los controladores con bloques `try-catch`:

| Excepción | Código HTTP | Descripción |
|---|---|---|
| `AccountNotFoundException` | `404 Not Found` | Cuenta no encontrada por identificador. |
| `InsufficientBalanceException` | `422 Unprocessable Content` | Saldo insuficiente para realizar el retiro. |
| `NegativeMoneyException` | `400 Bad Request` | Importe monetario menor o igual a cero. |
| `MethodArgumentNotValidException` | `400 Bad Request` | Fallos de validación en JSON de entrada (`@Valid`). |
| `IllegalArgumentException` | `400 Bad Request` | Formatos inválidos (ej: UUID corrupto). |

### Formato Estándar de Respuesta de Error (`ErrorMessage`):
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

## 🗺️ Roadmap & Siguientes Pasos

- [ ] **Transferencias entre Cuentas:** Caso de uso `TransferMoneyUseCase` para mover fondos entre dos agregados de forma transaccional (cumplimiento ACID).
- [ ] **Seguridad Bancaria:** Integración de Spring Security con autenticación y autorización mediante tokens JWT.
- [ ] **Tipo de Movimiento como Entidad/Catálogo:** Evolucionar `TransactionType` a una entidad de base de datos para soportar comisiones dinámicas, límites diarios y códigos de propósito ISO 20022.
- [ ] **Pruebas con Testcontainers:** Integración de contenedores efímeros de base de datos para ejecución automática en pipelines de CI/CD.
- [ ] **Documentación OpenAPI / Swagger:** Generación automática de especificación interactiva para consumo frontend.

---

## 👨‍💻 Autor & Licencia

Desarrollado como proyecto de ingeniería de software y arquitectura limpia para entornos bancarios y corporativos de alto nivel.
Licencia MIT.
