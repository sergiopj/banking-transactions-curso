package com.banking.transactions.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import com.banking.transactions.domain.exception.InsufficientBalanceException;
import com.banking.transactions.domain.exception.NegativeMoneyException;

/**
 * AGGREGATE ROOT - Único punto de entrada para gestionar balance e histórico
 * 
 * POR QUÉ public final class: public porque application/ e infrastructure/
 * necesitan acceder al agregado. final porque en DDD un Aggregate Root no se
 * hereda,
 * si lo heredas rompes las invariantes del banco. Es la raíz que protege todo.
 */
public final class Account {

    // POR QUÉ private final AccountId id: private para encapsular, final porque la
    // identidad
    // de una cuenta bancaria NUNCA cambia. Es inmutable.
    private final AccountId id;

    // POR QUÉ private final String customerId: final porque el dueño de la cuenta
    // no cambia.
    // Comentario "posible nueva entidad" correcto, a futuro sería CustomerId Value
    // Object.
    private final String customerId; // posible nueva entidad

    // POR QUÉ private Money balance (NO final): Es el único estado MUTABLE del
    // agregado.
    // Tiene que cambiar en cada deposit/withdraw. Si fuera final, la cuenta nunca
    // podría
    // mover dinero. Money en sí es inmutable, pero la referencia sí cambia.
    private Money balance; // mutable — changes on every deposit/withdraw

    // POR QUÉ private final List = new ArrayList<>(): final en la referencia de la
    // lista,
    // no en el contenido. La lista como objeto no se reasigna nunca (siempre es la
    // misma
    // instancia), pero su contenido crece. Es append-only, nunca se hace clear().
    // new ArrayList<>() inline evita NullPointer si se usa el constructor público.
    private final List<Transaction> transactions = new ArrayList<>(); // append-only, never cleared

    /**
     * POR QUÉ public constructor: Constructor de NEGOCIO para crear cuenta nueva.
     * public porque lo usa el caso de uso CreateAccountUseCase.
     * Valida con Objects.requireNonNull: invariante de dominio, una cuenta sin id
     * no existe.
     */
    public Account(AccountId id, String customerId, Money initialBalance) {
        this.id = Objects.requireNonNull(id, "Account id is required");
        this.customerId = Objects.requireNonNull(customerId, "Customer id is required");
        this.balance = Objects.requireNonNull(initialBalance, "Initial balance is required");
    }

    /**
     * POR QUÉ private constructor: Constructor de RECONSTITUCIÓN solo para DB.
     * private porque el negocio jamás debe crear una cuenta con balance ya
     * existente
     * y con transacciones pasadas. Solo el método reconstitute() puede llamarlo.
     */
    private Account(AccountId id, String customerId, Money balance, List<Transaction> transactions) {
        this.id = id;
        this.customerId = customerId;
        this.balance = balance;
        // Safe incluso si la BD devuelve null, usa List.of() vacía. Evita NPE.
        this.transactions.addAll(Objects.requireNonNullElse(transactions, List.of()));
    }

    /**
     * POR QUÉ public static reconstitute(): Factory Method de DDD.
     * public: lo necesita el Adapter de salida (JpaAdapter) para reconstruir.
     * static: no necesitas una cuenta previa para reconstruir una desde BD.
     * Separa claramente "crear cuenta nueva" de "cargar cuenta existente".
     */
    public static Account reconstitute(AccountId id, String customerId, Money balance, List<Transaction> transactions) {
        return new Account(id, customerId, balance, transactions);
    }

    /**
     * POR QUÉ public void deposit(): Método de negocio, parte de la API del
     * agregado.
     * public porque lo invocan los UseCases. No devuelve nada, modifica su propio
     * estado.
     * Añade Transaction internamente: el agregado controla su histórico, nadie más.
     */
    public void deposit(Money amount) {
        Objects.requireNonNull(amount, "Deposit amount cannot be null");
        if (!amount.isPositive()) {
            throw new NegativeMoneyException("Deposit amount must be greater than zero");
        }
        this.balance = this.balance.add(amount); // Money es inmutable, se reasigna nuevo objeto
        this.transactions.add(new Transaction(TransactionType.DEPOSIT, amount));
    }

    /**
     * POR QUÉ public void withdraw(): Misma idea que deposit pero con invariante
     * bancaria.
     * Lanza InsufficientBalanceException (excepción de dominio, no técnica).
     * El dominio protege que el balance nunca sea negativo.
     */
    public void withdraw(Money amount) {
        Objects.requireNonNull(amount, "Withdraw amount cannot be null");
        if (!amount.isPositive()) {
            throw new NegativeMoneyException("Withdraw amount must be greater than zero");
        }
        if (this.balance.subtract(amount).isNegative()) {
            throw new InsufficientBalanceException("Insufficient funds for withdrawal");
        }
        this.balance = this.balance.subtract(amount);
        this.transactions.add(new Transaction(TransactionType.WITHDRAW, amount));
    }

    // POR QUÉ solo getters: Exposición de lectura, sin setters. El único modo de
    // cambiar
    // el balance es via deposit()/withdraw(). Así proteges las reglas.
    public AccountId getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public Money getBalance() {
        return balance;
    }

    /**
     * POR QUÉ Collections.unmodifiableList: Defensa crítica en banca.
     * Si devolvieras la lista directa, cualquiera desde fuera podría hacer
     * account.getTransactions().clear() y borrar el histórico. Con unmodifiable
     * devuelves una vista de solo lectura. Append-only garantizado.
     */
    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }
}