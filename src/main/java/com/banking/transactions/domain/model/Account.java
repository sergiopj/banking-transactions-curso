package com.banking.transactions.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.banking.transactions.domain.exception.InsufficientBalanceException;
import com.banking.transactions.domain.exception.NegativeMoneyException;

// Aggregate Root — the only entry point to manage balance and transaction history
public final class Account {

    private final AccountId id;
    private final String customerId;
    private Money balance; // mutable — changes on every deposit/withdraw
    private final List<Transaction> transactions = new ArrayList<>(); // append-only, never cleared

    // Constructor for creating a new account
    public Account(AccountId id, String customerId, Money initialBalance) {
        this.id = Objects.requireNonNull(id, "Account id is required");
        this.customerId = Objects.requireNonNull(customerId, "Customer id is required");
        this.balance = Objects.requireNonNull(initialBalance, "Initial balance is required");
    }

    // Private constructor — only used by reconstitute() to reload from DB
    private Account(AccountId id, String customerId, Money balance, List<Transaction> transactions) {
        this.id = id;
        this.customerId = customerId;
        this.balance = balance;
        // Safe even if DB returns no transactions yet
        this.transactions.addAll(Objects.requireNonNullElse(transactions, List.of()));
    }

    // Factory method to rebuild an Account from persisted data (DB / repository)
    public static Account reconstitute(AccountId id, String customerId, Money balance, List<Transaction> transactions) {
        return new Account(id, customerId, balance, transactions);
    }

    // Adds money to the account and records the transaction
    public void deposit(Money amount) {
        Objects.requireNonNull(amount, "Deposit amount cannot be null");
        if (!amount.isPositive()) {
            throw new NegativeMoneyException("Deposit amount must be greater than zero");
        }
        this.balance = this.balance.add(amount);
        this.transactions.add(new Transaction(TransactionType.DEPOSIT, amount));
    }

    // Subtracts money from the account — throws domain exception if funds are
    // insufficient
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

    public AccountId getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public Money getBalance() {
        return balance;
    }

    // Returns an unmodifiable view — nobody can clear or add transactions from
    // outside
    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }

}
