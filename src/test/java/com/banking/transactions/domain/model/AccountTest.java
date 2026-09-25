package com.banking.transactions.domain.model;

import com.banking.transactions.domain.exception.InsufficientBalanceException;
import com.banking.transactions.domain.exception.NegativeMoneyException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Account Aggregate Root Tests")
class AccountTest {

    @Test
    @DisplayName("Should create new Account with valid data")
    void shouldCreateAccount() {
        AccountId id = AccountId.newId();
        Account account = new Account(id, "cust-1", Money.of("100.00"));

        assertEquals(id, account.getId());
        assertEquals("cust-1", account.getCustomerId());
        assertEquals(Money.of("100.00"), account.getBalance());
        assertTrue(account.getTransactions().isEmpty());
    }

    @Test
    @DisplayName("Should deposit money and append transaction")
    void shouldDepositMoney() {
        Account account = new Account(AccountId.newId(), "cust-1", Money.of("100.00"));

        account.deposit(Money.of("50.00"));

        assertEquals(Money.of("150.00"), account.getBalance());
        assertEquals(1, account.getTransactions().size());
        assertEquals(TransactionType.DEPOSIT, account.getTransactions().getFirst().getTransactionType());
        assertEquals(Money.of("50.00"), account.getTransactions().getFirst().getAmount());
    }

    @Test
    @DisplayName("Should reject deposit when amount is zero or negative")
    void shouldRejectInvalidDeposit() {
        Account account = new Account(AccountId.newId(), "cust-1", Money.of("100.00"));

        assertThrows(NegativeMoneyException.class, () -> account.deposit(Money.zero()));
        assertThrows(NegativeMoneyException.class, () -> account.deposit(Money.of("-10.00")));
        assertThrows(NullPointerException.class, () -> account.deposit(null));

        assertEquals(Money.of("100.00"), account.getBalance());
        assertTrue(account.getTransactions().isEmpty());
    }

    @Test
    @DisplayName("Should withdraw money and append transaction")
    void shouldWithdrawMoney() {
        Account account = new Account(AccountId.newId(), "cust-1", Money.of("100.00"));

        account.withdraw(Money.of("40.00"));

        assertEquals(Money.of("60.00"), account.getBalance());
        assertEquals(1, account.getTransactions().size());
        assertEquals(TransactionType.WITHDRAW, account.getTransactions().getFirst().getTransactionType());
        assertEquals(Money.of("40.00"), account.getTransactions().getFirst().getAmount());
    }

    @Test
    @DisplayName("Should reject withdraw when amount exceeds balance")
    void shouldRejectInsufficientFunds() {
        Account account = new Account(AccountId.newId(), "cust-1", Money.of("100.00"));

        assertThrows(InsufficientBalanceException.class, () -> account.withdraw(Money.of("150.00")));

        assertEquals(Money.of("100.00"), account.getBalance());
        assertTrue(account.getTransactions().isEmpty());
    }

    @Test
    @DisplayName("Should reject withdraw when amount is zero or negative")
    void shouldRejectInvalidWithdraw() {
        Account account = new Account(AccountId.newId(), "cust-1", Money.of("100.00"));

        assertThrows(NegativeMoneyException.class, () -> account.withdraw(Money.zero()));
        assertThrows(NegativeMoneyException.class, () -> account.withdraw(Money.of("-5.00")));

        assertEquals(Money.of("100.00"), account.getBalance());
    }

    @Test
    @DisplayName("Should reconstitute account with existing transactions from persistence")
    void shouldReconstituteAccount() {
        AccountId id = AccountId.newId();
        Transaction tx = new Transaction(TransactionType.DEPOSIT, Money.of("50.00"));
        Account account = Account.reconstitute(id, "cust-1", Money.of("150.00"), List.of(tx));

        assertEquals(id, account.getId());
        assertEquals(Money.of("150.00"), account.getBalance());
        assertEquals(1, account.getTransactions().size());
    }

    @Test
    @DisplayName("Transactions list returned by getter should be unmodifiable")
    void shouldProtectTransactionsListFromExternalMutation() {
        Account account = new Account(AccountId.newId(), "cust-1", Money.of("100.00"));
        Transaction tx = new Transaction(TransactionType.DEPOSIT, Money.of("10.00"));

        assertThrows(UnsupportedOperationException.class, () -> account.getTransactions().add(tx));
    }
}
