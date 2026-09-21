package com.banking.transactions.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import com.banking.transactions.domain.exception.NegativeMoneyException;

// TODO entender todo lo que hace esta clase, es un value object que representa una transaccion bancaria, con atributos inmutables como id, tipo de transaccion, monto y fecha de creacion. Tiene un constructor para crear una nueva transaccion y otro privado para reconstituirla desde datos persistidos. Ademas tiene metodos para obtener los atributos y sobreescribe equals, hashCode y toString.
// Immutable banking transaction representing an unalterable historical event (no setters)
public final class Transaction {

    private final String id;
    private final TransactionType transactionType;
    private final Money amount;
    private final Instant createdAt;

    // Constructor for creating a new transaction — generates its own id and
    // timestamp
    public Transaction(TransactionType transactionType, Money amount) {
        if (transactionType == null) {
            throw new IllegalArgumentException("Transaction type is required");
        }
        if (amount == null) {
            throw new IllegalArgumentException("Amount is required");
        }
        // Covers both zero and negative in one check — amount must always be a positive
        if (amount.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new NegativeMoneyException("Amount must be greater than zero");
        }

        this.id = UUID.randomUUID().toString();
        this.transactionType = transactionType;
        this.amount = amount;
        this.createdAt = Instant.now();
    }

    // Private constructor for reconstitution only — used when loading from DB
    private Transaction(String id, TransactionType transactionType, Money amount, Instant createdAt) {
        this.id = id;
        this.transactionType = transactionType;
        this.amount = amount;
        this.createdAt = createdAt;
    }

    // Factory method to rebuild a Transaction from persisted data (DB / repository)
    public static Transaction reconstitute(String id, TransactionType transactionType, Money amount,
            Instant createdAt) {
        return new Transaction(id, transactionType, amount, createdAt);
    }

    public String getId() {
        return id;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public Money getAmount() {
        return amount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Transaction that))
            return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + id + '\'' +
                ", transactionType=" + transactionType +
                ", amount=" + amount +
                ", createdAt=" + createdAt +
                '}';
    }

}
