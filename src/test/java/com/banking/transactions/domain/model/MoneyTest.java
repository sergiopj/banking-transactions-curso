package com.banking.transactions.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Money Value Object Tests")
class MoneyTest {

    @Nested
    @DisplayName("Creation and Invariants")
    class CreationTests {

        @Test
        @DisplayName("Should create Money from string with scale 2")
        void shouldCreateMoneyFromString() {
            Money money = Money.of("100.5");
            assertEquals(new BigDecimal("100.50"), money.getAmount());
        }

        @Test
        @DisplayName("Should apply HALF_UP rounding to two decimal places")
        void shouldApplyHalfUpRounding() {
            Money money = Money.of("100.555");
            assertEquals(new BigDecimal("100.56"), money.getAmount());
        }

        @Test
        @DisplayName("Should create zero Money")
        void shouldCreateZeroMoney() {
            Money zero = Money.zero();
            assertEquals(new BigDecimal("0.00"), zero.getAmount());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when value is null")
        void shouldThrowWhenNull() {
            assertThrows(IllegalArgumentException.class, () -> Money.of((String) null));
            assertThrows(IllegalArgumentException.class, () -> Money.from(null));
        }
    }

    @Nested
    @DisplayName("Operations")
    class OperationTests {

        @Test
        @DisplayName("Should add two Money instances")
        void shouldAddMoney() {
            Money m1 = Money.of("50.25");
            Money m2 = Money.of("25.75");
            Money result = m1.add(m2);

            assertEquals(Money.of("76.00"), result);
        }

        @Test
        @DisplayName("Should subtract two Money instances")
        void shouldSubtractMoney() {
            Money m1 = Money.of("100.00");
            Money m2 = Money.of("40.50");
            Money result = m1.subtract(m2);

            assertEquals(Money.of("59.50"), result);
        }

        @Test
        @DisplayName("Should evaluate isPositive and isNegative correctly")
        void shouldEvaluateSigns() {
            Money positive = Money.of("10.00");
            Money zero = Money.zero();
            Money negative = Money.from(new BigDecimal("-5.00"));

            assertTrue(positive.isPositive());
            assertFalse(positive.isNegative());

            assertFalse(zero.isPositive());
            assertFalse(zero.isNegative());

            assertFalse(negative.isPositive());
            assertTrue(negative.isNegative());
        }

        @Test
        @DisplayName("Money.of() should reject negative amounts with NegativeMoneyException")
        void shouldRejectNegativeStringInOf() {
            assertThrows(com.banking.transactions.domain.exception.NegativeMoneyException.class,
                    () -> Money.of("-5.00"));
        }
    }

    @Nested
    @DisplayName("Equality and Comparison")
    class EqualityTests {

        @Test
        @DisplayName("Two Money with same value should be equal regardless of initial representation")
        void shouldBeEqualByValue() {
            Money m1 = Money.of("100");
            Money m2 = Money.of("100.00");

            assertEquals(m1, m2);
            assertEquals(m1.hashCode(), m2.hashCode());
        }

        @Test
        @DisplayName("Should compare Money using compareTo")
        void shouldCompareCorrectly() {
            Money smaller = Money.of("50.00");
            Money larger = Money.of("100.00");

            assertTrue(smaller.compareTo(larger) < 0);
            assertTrue(larger.compareTo(smaller) > 0);
            assertEquals(0, smaller.compareTo(Money.of("50.00")));
        }
    }
}
