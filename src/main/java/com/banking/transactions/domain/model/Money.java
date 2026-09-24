package com.banking.transactions.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import com.banking.transactions.domain.exception.NegativeMoneyException;

/**
 * Value Object inmutable - Dinero. public para el Core, final para no heredar y
 * proteger equals/inmutabilidad. Envuelve BigDecimal siempre con 2 decimales y
 * HALF_UP, nunca double.
 */
public final class Money {

    private final BigDecimal amount; // final = inmutable total

    /** Constructor privado: fuerza fábricas y centraliza regla de 2 decimales */
    private Money(BigDecimal amount) {
        this.amount = amount.setScale(2, RoundingMode.HALF_UP); // regla bancaria
    }

    /**
     * Factory desde texto (JSON/DTO). Usa BigDecimal(String) exacto, no double.
     * Valida negativo
     */
    public static Money of(String value) {
        if (value == null)
            throw new IllegalArgumentException("Amount cannot be null");
        String trimmed = value.trim();
        if (trimmed.isEmpty())
            throw new IllegalArgumentException("Amount cannot be empty");
        try {
            BigDecimal decimal = new BigDecimal(trimmed);
            if (decimal.compareTo(BigDecimal.ZERO) < 0)
                throw new NegativeMoneyException("Amount cannot be negative: " + value);
            return new Money(decimal);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid amount format: " + value, ex);
        }
    }

    /**
     * Fix para Command con Double nullable: null->zero, si no String.valueOf para
     * evitar BigDecimal(double) inexacto
     */
    public static Money of(Double value) {
        return value == null ? Money.of("0") : Money.of(String.valueOf(value));
    }

    /** Factory desde BigDecimal ya existente, preferida para cálculos internos */
    public static Money from(BigDecimal value) {
        if (value == null)
            throw new IllegalArgumentException("Amount cannot be null");
        return new Money(value);
    }

    /** Constante de fábrica: evita repetir Money.of("0") */
    public static Money zero() {
        return new Money(BigDecimal.ZERO);
    }

    /** Suma inmutable: no modifica this, devuelve nuevo Money. Permite encadenar */
    public Money add(Money other) {
        Objects.requireNonNull(other, "Other money cannot be null");
        return new Money(this.amount.add(other.amount));
    }

    /** Resta inmutable para withdraw: misma lógica que add() */
    public Money subtract(Money other) {
        Objects.requireNonNull(other, "Other money cannot be null");
        return new Money(this.amount.subtract(other.amount));
    }

    /**
     * Query de estado <0 sin exponer BigDecimal. Usa compareTo, nunca < > ni equals
     */
    public boolean isNegative() {
        return this.amount.compareTo(BigDecimal.ZERO) < 0;
    }

    /** Query de estado >0 para validar deposit/withdraw */
    public boolean isPositive() {
        return this.amount.compareTo(BigDecimal.ZERO) > 0;
    }

    /** Getter safe: BigDecimal ya es inmutable */
    public BigDecimal getAmount() {
        return this.amount;
    }

    /** Igualdad por valor, no por escala: 10.00 == 10.0 debe ser true en banca */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Money money))
            return false;
        return this.amount.compareTo(money.amount) == 0;
    }

    /**
     * Hash compatible con equals: stripTrailingZeros para que 10.00 y 10.0 tengan
     * mismo hash
     */
    @Override
    public int hashCode() {
        return amount.stripTrailingZeros().hashCode();
    }

    /** Evita notación científica 1E+2, devuelve "100.00" limpio para logs */
    @Override
    public String toString() {
        return amount.toPlainString();
    }
}