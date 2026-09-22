package com.banking.transactions.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import com.banking.transactions.domain.exception.NegativeMoneyException;

/**
 * ENTIDAD DE DOMINIO - Evento histórico inmutable
 * 
 * POR QUÉ public final class: public para ser accesible desde application e
 * infrastructure.
 * final: Nadie puede heredar de Transaction. En DDD una transacción bancaria es
 * un hecho
 * cerrado, no se extiende. Garantiza inmutabilidad real.
 * 
 * POR QUÉ private final fields: private para encapsular, nadie toca el estado
 * directo.
 * final para inmutabilidad: una vez creada, nunca cambia. Sin setters. Así
 * evitas fraude
 * o modificación del histórico en banca.
 */
public final class Transaction {

    private final String id;
    private final TransactionType transactionType;
    private final Money amount;
    private final Instant createdAt;

    /**
     * POR QUÉ public constructor: Es el constructor de NEGOCIO. Lo usa el dominio
     * cuando haces account.deposit(). Genera su propio id y timestamp, el exterior
     * no puede decidirlos. Aquí valida invariantes.
     */
    public Transaction(TransactionType transactionType, Money amount) {
        if (transactionType == null) {
            throw new IllegalArgumentException("Transaction type is required");
        }
        if (amount == null) {
            throw new IllegalArgumentException("Amount is required");
        }
        if (amount.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new NegativeMoneyException("Amount must be greater than zero");
        }

        this.id = UUID.randomUUID().toString(); // El dominio genera su identidad
        this.transactionType = transactionType;
        this.amount = amount;
        this.createdAt = Instant.now(); // El dominio genera su timestamp
    }

    /**
     * POR QUÉ private constructor: Constructor de RECONSTITUCIÓN. Solo para
     * reconstruir desde BD. Es private porque el negocio nunca debe usarlo,
     * solo el método reconstitute().
     */
    private Transaction(String id, TransactionType transactionType, Money amount, Instant createdAt) {
        this.id = id;
        this.transactionType = transactionType;
        this.amount = amount;
        this.createdAt = createdAt;
    }

    /**
     * POR QUÉ public static reconstitute(): Factory Method de DDD.
     * public: lo necesita infrastructure (JpaAdapter) para reconstruir.
     * static: no necesitas una instancia previa para reconstruir.
     * Nombre reconstitute: deja claro que no es una creación nueva de negocio,
     * es una reconstrucción desde persistencia.
     */
    public static Transaction reconstitute(String id, TransactionType transactionType, Money amount,
            Instant createdAt) {
        return new Transaction(id, transactionType, amount, createdAt);
    }

    // POR QUÉ solo getters y no setters: Inmutabilidad. Lees, pero no modificas.
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

    // equals/hashCode solo por id: En DDD dos Transactions son la misma entidad
    // si tienen el mismo id, aunque cambie lo demás.
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