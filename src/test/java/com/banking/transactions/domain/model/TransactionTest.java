package com.banking.transactions.domain.model;

import com.banking.transactions.domain.exception.NegativeMoneyException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Transaction Entity Tests")
class TransactionTest {

    @Test
    @DisplayName("Should create valid Transaction with auto-generated id and createdAt")
    void shouldCreateValidTransaction() {
        Money amount = Money.of("100.00");
        Transaction tx = new Transaction(TransactionType.DEPOSIT, amount);

        assertNotNull(tx.getId());
        assertEquals(TransactionType.DEPOSIT, tx.getTransactionType());
        assertEquals(amount, tx.getAmount());
        assertNotNull(tx.getCreatedAt());
        assertTrue(tx.getCreatedAt().isBefore(Instant.now().plusSeconds(1)));
    }

    @Test
    @DisplayName("Should throw when TransactionType is null")
    void shouldThrowWhenTypeIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new Transaction(null, Money.of("50.00"))
        );
    }

    @Test
    @DisplayName("Should throw when Amount is null or zero/negative")
    void shouldThrowWhenAmountIsInvalid() {
        assertThrows(IllegalArgumentException.class, () ->
                new Transaction(TransactionType.DEPOSIT, null)
        );

        assertThrows(NegativeMoneyException.class, () ->
                new Transaction(TransactionType.DEPOSIT, Money.zero())
        );

        assertThrows(NegativeMoneyException.class, () ->
                new Transaction(TransactionType.DEPOSIT, Money.of("-10.00"))
        );
    }

    @Test
    @DisplayName("Should reconstitute Transaction from persistence without generating new ID")
    void shouldReconstituteTransaction() {
        String fixedId = "fixed-uuid-123";
        Instant fixedDate = Instant.parse("2026-01-01T10:00:00Z");
        Money amount = Money.of("200.00");

        Transaction tx = Transaction.reconstitute(fixedId, TransactionType.WITHDRAW, amount, fixedDate);

        assertEquals(fixedId, tx.getId());
        assertEquals(TransactionType.WITHDRAW, tx.getTransactionType());
        assertEquals(amount, tx.getAmount());
        assertEquals(fixedDate, tx.getCreatedAt());
    }

    @Test
    @DisplayName("Transactions with same ID should be equal")
    void shouldBeEqualById() {
        Instant now = Instant.now();
        Transaction tx1 = Transaction.reconstitute("tx-1", TransactionType.DEPOSIT, Money.of("50.00"), now);
        Transaction tx2 = Transaction.reconstitute("tx-1", TransactionType.WITHDRAW, Money.of("999.00"), now);

        assertEquals(tx1, tx2);
        assertEquals(tx1.hashCode(), tx2.hashCode());
    }
}
