package com.banking.transactions.model.domain;

import com.banking.transactions.domain.model.Account;
import com.banking.transactions.domain.model.AccountId;
import com.banking.transactions.domain.model.Money;
import com.banking.transactions.domain.model.TransactionType;
import com.banking.transactions.domain.exception.InsufficientBalanceException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

// TODO hacer yo el resto de tests, y entender que se esta probando y que se espera, en este caso que el balance sea 150.00 y que la transaccion sea de tipo DEPOSIT

class AccountTest {

    // AAA pattern:
    // Arrange - initialize the account with a specific balance
    // Act - perform the deposit action
    // Assert - verify the balance has increased by the deposited amount
    @Test
    void should_deposit_money_into_account() {
        // Arrange
        Account account = new Account(AccountId.newId(), "123", Money.of("100.00"));

        // Act
        account.deposit(Money.of("50.00"));

        // Assert
        // TODO entender que se esta probando y que se espera, en este caso que el
        // balance sea 150.00 y que la transaccion sea de tipo DEPOSIT
        assertEquals(Money.of("150.00").getAmount(), account.getBalance().getAmount());
        assertFalse(account.getTransactions().isEmpty(), "Transaction history should not be empty after deposit");
        assertEquals(TransactionType.DEPOSIT, account.getTransactions().getFirst().getTransactionType(),
                "The first transaction should be a deposit");
    }

    @Test
    void should_not_allow_insufficient_balance_withdrawal() {
        // Arrange
        Account account = new Account(AccountId.newId(), "123", Money.of("100.00"));

        // Act
        InsufficientBalanceException exception = assertThrows(InsufficientBalanceException.class, () -> {
            account.withdraw(Money.of("150.00"));
        });

        // Assert
        assertNotNull(exception.getMessage());
        assertEquals(Money.of("100.00"), account.getBalance());
        assertTrue(account.getTransactions().isEmpty());
    }

}
