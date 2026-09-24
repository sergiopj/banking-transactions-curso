package com.banking.transactions.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import com.banking.transactions.domain.exception.InsufficientBalanceException;
import com.banking.transactions.domain.exception.NegativeMoneyException;

/**
 * Aggregate Root - único entry point para balance e histórico.
 * public para app/infra, final para no heredar y no romper invariantes.
 */
public final class Account {

    private final AccountId id; // identidad inmutable, nunca cambia
    private final String customerId; // dueño inmutable, futuro VO CustomerId
    private Money balance; // único mutable. Money es inmutable, pero la ref se reasigna
    private final List<Transaction> transactions = new ArrayList<>(); // ref final, contenido append-only, nunca clear()

    /**
     * Constructor de negocio para cuenta nueva. Valida invariantes con
     * requireNonNull
     */
    public Account(AccountId id, String customerId, Money initialBalance) {
        this.id = Objects.requireNonNull(id, "Account id is required");
        this.customerId = Objects.requireNonNull(customerId, "Customer id is required");
        this.balance = Objects.requireNonNull(initialBalance, "Initial balance is required");
    }

    /**
     * Constructor de reconstitución solo para DB. private para que negocio no lo
     * use
     */
    private Account(AccountId id, String customerId, Money balance, List<Transaction> transactions) {
        this.id = id;
        this.customerId = customerId;
        this.balance = balance;
        this.transactions.addAll(Objects.requireNonNullElse(transactions, List.of())); // safe si BD devuelve null
    }

    /**
     * Factory DDD para reconstruir desde persistence. Separa "crear nueva" vs
     * "cargar existente"
     */
    public static Account reconstitute(AccountId id, String customerId, Money balance, List<Transaction> transactions) {
        return new Account(id, customerId, balance, transactions);
    }

    /**
     * Regla de negocio deposit: valida >0, suma balance y añade Transaction interna
     */
    public void deposit(Money amount) {
        Objects.requireNonNull(amount, "Deposit amount cannot be null");
        if (!amount.isPositive()) {
            throw new NegativeMoneyException("Deposit amount must be greater than zero");
        }
        this.balance = this.balance.add(amount);
        this.transactions.add(new Transaction(TransactionType.DEPOSIT, amount));
    }

    /**
     * Regla de negocio withdraw: valida >0 y saldo suficiente, protege balance
     * nunca negativo
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

    // Solo getters, sin setters: balance solo cambia via deposit()/withdraw()
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
     * Vista de solo lectura: evita account.getTransactions().clear() y borrado de
     * histórico
     */
    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }
}