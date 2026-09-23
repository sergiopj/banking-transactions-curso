# Arquitectura Hexagonal - Banking Transactions API

Este documento detalla la estructura por capas, el flujo de peticiones y las decisiones técnicas de diseño aplicadas en este proyecto bajo **Arquitectura Hexagonal (Ports & Adapters)** y **Domain-Driven Design (DDD)**.

---

## 🏛️ Visión General: Las 3 Capas Fundamentales

La premisa central es el **Principio de Inversión de Dependencias (DIP)**: el núcleo del negocio es el centro y **no depende de ningún framework, base de datos ni transporte**. Todo lo externo se adapta a él.

```
       [ ADAPTADOR ENTRADA ]         --> (HTTP REST Controller)
                │
                ▼
       [ PUERTO ENTRADA ]            --> (CreateAccountUseCase interface)
                │
┌───────────────┼────────────────────────────────────────┐
│               ▼                                        │
│     [ CAPA DE APLICACIÓN ]                             │
│       - Orquestación (CreateAccountService)            │
│       - Commands & DTOs inmutables                     │
│               │                                        │
│               ▼                                        │
│     [ CAPA DE DOMINIO ] (Core Agnóstico)               │
│       - Agregados & Entidades (Account, Transaction)   │
│       - Value Objects (Money, AccountId)               │
│       - Reglas e invariantes de negocio                │
│       - Excepciones de negocio (InsufficientFunds)     │
│               │                                        │
│               ▼                                        │
│     [ PUERTO DE SALIDA ]                               │
│       - Interfaz (AccountRepository)                   │
└───────────────┼────────────────────────────────────────┘
                │
                ▼
       [ ADAPTADOR SALIDA ]          --> (AccountJpaAdapter / MySQL)
```

---

## 📦 1. Capa de Dominio (`domain/`) — El Núcleo Puro

Es el corazón del sistema bancario. **100% Java puro**: sin dependencias de Spring Boot, JPA, Hibernate ni librerías de infraestructura.

* **`domain/model/` (Entidades y Value Objects):**
  * **`Account` (Aggregate Root):** La entidad principal y única guardiana del estado. Controla las operaciones de ingreso y retiro (`deposit`, `withdraw`), garantizando que el saldo nunca sea negativo ni se modifique sin dejar traza histórica.
  * **`Transaction` (Entity Inmutable):** Representa un evento histórico inalterable en el tiempo. Solo tiene constructor, validaciones *fail-fast*, identidad propia (`UUID`) y **carece de setters**.
  * **`Money` (Value Object):** Encapsula importes monetarios usando `BigDecimal` con escala forzada a 2 decimales y redondeo bancario `HALF_UP`. Previene problemas de coma flotante de tipos como `double`. Inmutable y comparable con `compareTo()`.
  * **`AccountId` (Value Object / Record):** Identificador fuertemente tipado para evitar confusiones de cadenas de texto primitivas.
* **`domain/port/` (Puertos de Salida - Driven/Outbound Ports):**
  * **`AccountRepository`:** Interfaz Java pura que declara qué operaciones de persistencia necesita el dominio (`save`, `findById`), sin saber si detrás hay MySQL, PostgreSQL o una memoria temporal.
* **`domain/exception/`:**
  * Excepciones de negocio (`InsufficientBalanceException`, `AccountNotFoundException`, `NegativeMoneyException`). Separan claramente errores de negocio de fallos técnicos.

---

## ⚙️ 2. Capa de Aplicación (`application/`) — La Orquestación

Coordina y orquesta los casos de uso. Conecta los adaptadores de entrada con el dominio y delega a los puertos de salida.

* **`application/port/` (Puertos de Entrada - Driving/Inbound Ports):**
  * Interfaces que definen los casos de uso que el sistema ofrece al exterior:
    * `CreateAccountUseCase`
    * `DepositMoneyUseCase`
    * `WithdrawMoneyUseCase`
    * `GetAccountDetailsUseCase`
* **`application/service/` (Implementación de Casos de Uso):**
  * Clases como `CreateAccountService`, `DepositMoneyService`, `WithdrawMoneyService`.
  * Inyectan los puertos (`AccountRepository`), inician transacciones (`@Transactional`), cargan agregados del dominio, ejecutan operaciones de negocio (`account.deposit()`) y guardan el resultado.
* **`application/dto/` (Commands, DTOs y Mappers):**
  * **Commands (Entrada):** `CreateAccountCommand`, `DepositMoneyCommand`, `WithdrawMoneyCommand`. Records inmutables que encapsulan los datos necesarios para un caso de uso (Write Model).
  * **DTOs (Salida):** `AccountDetailsDto`, `TransactionDto`. Modelos de lectura que viajan hacia fuera sin exponer las entidades de dominio (Anti-Corruption Layer).
  * **Mappers:** `MapToAccountDetailsDto`. Función pura estática encargada de transformar entidades `Account` a DTOs de salida.

---

## 🔌 3. Capa de Infraestructura (`infrastructure/`) — Adaptadores Tecnológicos

Aquí vive toda la tecnología: Spring Framework, Spring MVC, Spring Data JPA, Hibernate, MySQL/Postgres, Jackson y Docker.

* **Adaptadores de Entrada (Inbound / Driving Adapters):**
  * Controladores REST (`@RestController`).
  * Reciben peticiones HTTP (JSON), validan el payload con anotaciones Bean Validation (`@Valid`, `@NotNull`), convierten la petición HTTP a un `Command` de aplicación y llaman al UseCase.
  * Capturan excepciones de dominio y las traducen a respuestas HTTP limpias mediante `@ControllerAdvice` (ej. `InsufficientBalanceException` $\rightarrow$ `422 Unprocessable Entity` o `400 Bad Request`).
* **Adaptadores de Salida (Outbound / Driven Adapters):**
  * Implementan los puertos de salida definidos en el dominio (`AccountRepository`).
  * **`AccountJpaAdapter`**: Implementa `AccountRepository` inyectando `SpringDataAccountRepository`. Convierte entre entidades JPA (`AccountEntity`) y el agregado de dominio (`Account`).
  * **`InMemoryAccountRepository`**: Implementación temporal en memoria para desarrollo o pruebas aisladas.

---

## 🔄 Flujo Completo de una Petición (Request-to-Response)

Tomando como ejemplo un **Ingreso de Dinero (Deposit)**:

```
[Cliente / Frontend]
       │
       │ 1. POST /api/v1/accounts/{id}/deposit (JSON: { "amount": 50.00 })
       ▼
[AccountController] (Adaptador Inbound)
       │
       │ 2. Parsea JSON y valida HTTP -> Crea DepositMoneyCommand
       ▼
[DepositMoneyUseCase] (Puerto de Entrada - Interface)
       │
       ▼
[DepositMoneyService] (Servicio de Aplicación)
       │
       │ 3. Llama a accountRepository.findById(accountId)
       ▼
[AccountRepository] (Puerto de Salida - Interface)
       │
       ▼
[AccountJpaAdapter] (Adaptador Outbound)
       │
       │ 4. Consulta BD MySQL / Postgres mediante JPA Entity
       ▼
[Base de Datos] ──(devuelve estado persistido)──► [AccountJpaAdapter]
       │
       │ 5. Reconstruye el agregado de dominio: Account.reconstitute(...)
       ▼
[DepositMoneyService]
       │
       │ 6. Ejecuta regla de negocio en el Core:
       │    account.deposit(Money.of("50.00"))
       │    - Valida que amount > 0
       │    - Suma balance
       │    - Añade Transaction(DEPOSIT) inmutable al historial
       │
       │ 7. Guarda cambios: accountRepository.save(account)
       ▼
[AccountJpaAdapter] ──(guarda cambios)──► [Base de Datos]
       │
       │ 8. Mapper traduce de Dominio a DTO:
       │    MapToAccountDetailsDto.from(account)
       ▼
[AccountController]
       │
       │ 9. Retorna 200 OK con JSON al cliente
       ▼
[Cliente / Frontend]
```

---

## 💡 ¿Por qué esta arquitectura es estándar en Banca y Sistemas Críticos?

1. **Aislamiento Total del Negocio:** Si se migra de base de datos relacional (MySQL) a NoSQL (MongoDB), o de REST a eventos asíncronos (Kafka), **las carpetas `domain/` y `application/` no se tocan**.
2. **Altísima Testeabilidad:** El dominio y los servicios se testean con tests unitarios instantáneos sin levantar Tomcat, Spring Context ni bases de datos.
3. **Inmutabilidad y Auditoría:** Al no haber setters en `Transaction` ni `Money`, es imposible alterar importes o fechas por accidente en ejecución.
4. **Separación de Responsabilidades:** El controller solo atiende HTTP, el caso de uso solo orquesta y el agregado solo garantiza sus reglas de negocio financieras.
