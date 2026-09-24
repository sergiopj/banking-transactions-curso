package com.banking.transactions.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import com.banking.transactions.domain.exception.NegativeMoneyException;

/**
 * Entidad de dominio - Evento histórico inmutable.
 * public para app/infra, final para no heredar: hecho bancario cerrado.
 * Campos private final sin setters -> inmutabilidad real, evita fraude en
 * histórico.
 */
public final class Transaction {

    private final String id;
    private final TransactionType transactionType;
    private final Money amount;
    private final Instant createdAt;

    /**
     * Constructor de negocio: genera id y timestamp propios, valida invariantes.
     * Usado por Account.deposit/withdraw
     */
    public Transaction(TransactionType transactionType, Money amount) {
        if (transactionType == null)
            throw new IllegalArgumentException("Transaction type is required");
        if (amount == null)
            throw new IllegalArgumentException("Amount is required");
        if (amount.getAmount().compareTo(BigDecimal.ZERO) <= 0)
            throw new NegativeMoneyException("Amount must be greater than zero");

        this.id = UUID.randomUUID().toString(); // identidad generada por dominio
        this.transactionType = transactionType;
        this.amount = amount;
        this.createdAt = Instant.now(); // timestamp del dominio, no del exterior
    }

    /** Constructor privado de reconstitución solo para DB */
    private Transaction(String id, TransactionType transactionType, Money amount, Instant createdAt) {
        this.id = id;
        this.transactionType = transactionType;
        this.amount = amount;
        this.createdAt = createdAt;
    }

    /** Factory DDD para reconstruir desde persistence. No es creación de negocio */
    public static Transaction reconstitute(String id, TransactionType transactionType, Money amount,
            Instant createdAt) {
        return new Transaction(id, transactionType, amount, createdAt);
    }

    // Solo getters -> solo lectura, sin modificación
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

    /** Entidad por identidad: mismo id = misma transacción */
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
}