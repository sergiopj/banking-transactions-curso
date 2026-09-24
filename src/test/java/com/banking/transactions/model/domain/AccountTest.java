package com.banking.transactions.model.domain;

import com.banking.transactions.domain.model.Account;
import com.banking.transactions.domain.model.AccountId;
import com.banking.transactions.domain.model.Money;
import com.banking.transactions.domain.model.TransactionType;
import com.banking.transactions.domain.exception.InsufficientBalanceException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test unitario del Aggregate Account - solo dominio, sin infra.
 * Patrón AAA: Arrange / Act / Assert
 */
class AccountTest {

    @Test
    void should_deposit_money_into_account() {
        // Arrange: cuenta nueva con 100.00
        Account account = new Account(AccountId.newId(), "123", Money.of("100.00"));

        // Act: deposita 50.00
        account.deposit(Money.of("50.00"));

        // Assert: balance = 150.00 + 1 transacción tipo DEPOSIT en histórico
        assertEquals(Money.of("150.00").getAmount(), account.getBalance().getAmount());
        assertFalse(account.getTransactions().isEmpty(), "Debe haber histórico tras deposit");
        assertEquals(TransactionType.DEPOSIT, account.getTransactions().getFirst().getTransactionType());
    }

    @Test
    void should_not_allow_insufficient_balance_withdrawal() {
        // Arrange: cuenta con 100.00
        Account account = new Account(AccountId.newId(), "123", Money.of("100.00"));

        // Act + Assert: retirar 150.00 -> lanza InsufficientBalanceException, balance
        // intacto y sin transacciones
        InsufficientBalanceException ex = assertThrows(InsufficientBalanceException.class, () -> {
            account.withdraw(Money.of("150.00"));
        });

        assertNotNull(ex.getMessage());
        assertEquals(Money.of("100.00"), account.getBalance());
        assertTrue(account.getTransactions().isEmpty());
    }

    // TODO resto: withdraw ok, deposit <=0, withdraw <=0, reconstitute
}