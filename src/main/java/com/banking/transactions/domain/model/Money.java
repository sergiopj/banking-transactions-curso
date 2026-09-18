package com.banking.transactions.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

// Immutable Value Object representing money (DDD)
public final class Money {

    // Monetary amount, always 2 decimal places
    private final BigDecimal amount;

    // Private constructor — forces use of factory methods
    private Money(BigDecimal amount) {
        this.amount = amount.setScale(2, RoundingMode.HALF_UP);
    }

    // Creates Money from String — useful for parsing user input
    public static Money of(String value) {
        if (value == null)
            throw new IllegalArgumentException("Amount cannot be null");
        return new Money(new BigDecimal(value));
    }

    // Creates Money from BigDecimal — preferred for calculations
    public static Money from(BigDecimal value) {
        if (value == null)
            throw new IllegalArgumentException("Amount cannot be null");
        return new Money(value);
    }

    // Returns a Money representing zero
    public static Money zero() {
        return new Money(BigDecimal.ZERO);
    }

    // Adds money (deposit / credit)
    public Money add(Money other) {
        Objects.requireNonNull(other, "Other money cannot be null");
        return new Money(this.amount.add(other.amount));
    }

    // Subtracts money (withdrawal / debit)
    public Money subtract(Money other) {
        return new Money(this.amount.subtract(other.amount));
    }

    // Checks if amount is negative (< 0)
    public boolean isNegative() {
        return this.amount.compareTo(BigDecimal.ZERO) < 0;
    }

    // Returns the BigDecimal amount
    public BigDecimal getAmount() {
        return this.amount;
    }

    // Override equals and hashCode for value object semantics
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Money money))
            return false;
        return this.amount.compareTo(money.amount) == 0;
    }

    @Override
    public int hashCode() {
        return amount.stripTrailingZeros().hashCode();
    }

    @Override
    public String toString() {
        return amount.toPlainString();
    }

}
