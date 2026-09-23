package com.banking.transactions.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import com.banking.transactions.domain.exception.NegativeMoneyException;

/**
 * VALUE OBJECT INMUTABLE - Representa dinero (DDD)
 * 
 * POR QUÉ public final class: public para usarse en todo el Core.
 * final: Un Value Object nunca se hereda. Si heredas de Money rompes la
 * inmutabilidad y el equals. En banca, Money es sagrado y cerrado.
 */
public final class Money {

    // POR QUÉ private final BigDecimal: private para encapsular, final para
    // inmutabilidad total. Una vez creado, nunca cambia de valor.
    // Siempre con 2 decimales, como en banca real.
    private final BigDecimal amount;

    /**
     * POR QUÉ private constructor: Fuerza el uso de fábricas.
     * private porque el exterior no debe hacer new Money(new
     * BigDecimal("10.123456")).
     * Aquí centralizas la regla de negocio: siempre 2 decimales con HALF_UP.
     * Si fuera public, cada dev podría crear Money con distinta escala.
     */
    private Money(BigDecimal amount) {
        // Regla bancaria: 2 decimales, redondeo bancario HALF_UP (1.005 -> 1.01)
        this.amount = amount.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * POR QUÉ public static of(String): Factory Method para parsear input de
     * usuario.
     * public: lo necesita application/ para convertir el DTO.
     * static: no necesitas una instancia previa de Money para crear una.
     * of(String): semántica para crear desde texto (JSON, formulario) usando
     * BigDecimal(String) que es exacto, no BigDecimal(double).
     */
    public static Money of(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Amount cannot be empty");
        }
        try {
            BigDecimal decimal = new BigDecimal(trimmed);
            // En banca el saldo inicial no puede ser negativo
            if (decimal.compareTo(BigDecimal.ZERO) < 0) {
                throw new NegativeMoneyException("Amount cannot be negative: " + value);
            }
            return new Money(decimal);
        } catch (NumberFormatException ex) {
            // Para que no te suba un NumberFormatException feo al Controller
            throw new IllegalArgumentException("Invalid amount format: " + value, ex);
        }
    }

    // FIX para tu error del CreateAccountService, añádelo en la misma clase:
    public static Money of(Double value) {
        return value == null ? Money.of("0") : Money.of(String.valueOf(value));
    }

    /**
     * POR QUÉ public static from(BigDecimal): Factory preferida para cálculos.
     * from(BigDecimal): semántica "desde un BigDecimal ya existente".
     * Separas of(String) vs from(BigDecimal) para dejar clara la intención.
     */
    public static Money from(BigDecimal value) {
        if (value == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        return new Money(value);
    }

    /**
     * POR QUÉ public static zero(): Value Object especial, dinero cero.
     * static porque es una constante de fábrica, no necesita instancia.
     * Evitas hacer Money.of("0") por todo el código.
     */
    public static Money zero() {
        return new Money(BigDecimal.ZERO);
    }

    /**
     * POR QUÉ public Money add(): Operación de dominio, inmutable.
     * public porque es la API del Value Object.
     * No es static: opera sobre this + other.
     * Devuelve new Money: inmutabilidad. No modifica this, crea uno nuevo.
     * Así encadenas: balance.add(deposit).subtract(fee)
     */
    public Money add(Money other) {
        Objects.requireNonNull(other, "Other money cannot be null");
        return new Money(this.amount.add(other.amount));
    }

    // Misma lógica que add() pero para débito / retirada
    public Money subtract(Money other) {
        Objects.requireNonNull(other, "Other money cannot be null");
        return new Money(this.amount.subtract(other.amount));
    }

    // POR QUÉ public boolean isNegative(): Query del Value Object.
    // El dominio pregunta el estado sin exponer el BigDecimal.
    // Usas compareTo, nunca equals ni < > con BigDecimal.
    public boolean isNegative() {
        return this.amount.compareTo(BigDecimal.ZERO) < 0;
    }

    public boolean isPositive() {
        return this.amount.compareTo(BigDecimal.ZERO) > 0;
    }

    // Getter simple, devuelve el BigDecimal interno (que ya es inmutable)
    public BigDecimal getAmount() {
        return this.amount;
    }

    /**
     * POR QUÉ equals con compareTo: Value Object por valor, no por referencia.
     * new Money("10.00") == new Money("10.0") debe ser true en banca.
     * Por eso compareTo == 0 y no equals, que fallaría por escala.
     * hashCode con stripTrailingZeros(): para que 10.00 y 10.0 tengan mismo hash.
     */
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

    // toPlainString(): evita notación científica 1E+2, devuelve "100.00" limpio
    // para logs
    @Override
    public String toString() {
        return amount.toPlainString();
    }
}