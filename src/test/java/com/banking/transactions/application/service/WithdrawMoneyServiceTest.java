package com.banking.transactions.application.service;

import com.banking.transactions.application.dto.AccountDetailsDto;
import com.banking.transactions.application.dto.WithdrawMoneyCommand;
import com.banking.transactions.domain.exception.AccountNotFoundException;
import com.banking.transactions.domain.exception.InsufficientBalanceException;
import com.banking.transactions.domain.model.Account;
import com.banking.transactions.domain.model.AccountId;
import com.banking.transactions.domain.model.Money;
import com.banking.transactions.domain.port.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("WithdrawMoneyService Unit Tests")
class WithdrawMoneyServiceTest {

    @Mock
    private AccountRepository accountRepository;

    private WithdrawMoneyService service;

    @BeforeEach
    void setUp() {
        service = new WithdrawMoneyService(accountRepository);
    }

    @Test
    @DisplayName("Should successfully withdraw money from account")
    void shouldWithdrawSuccessfully() {
        AccountId accountId = AccountId.newId();
        Account existingAccount = new Account(accountId, "cust-1", Money.of("200.00"));

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(existingAccount));
        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));

        WithdrawMoneyCommand command = new WithdrawMoneyCommand(accountId.value(), 75.00);
        AccountDetailsDto result = service.withdrawMoney(command);

        assertNotNull(result);
        assertEquals(125.00, result.balance());
        assertEquals(1, result.transactions().size());

        verify(accountRepository, times(1)).save(existingAccount);
    }

    @Test
    @DisplayName("Should propagate InsufficientBalanceException and avoid saving state")
    void shouldPropagateInsufficientBalance() {
        AccountId accountId = AccountId.newId();
        Account existingAccount = new Account(accountId, "cust-1", Money.of("50.00"));

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(existingAccount));

        WithdrawMoneyCommand command = new WithdrawMoneyCommand(accountId.value(), 100.00);

        assertThrows(InsufficientBalanceException.class, () -> service.withdrawMoney(command));
        verify(accountRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw AccountNotFoundException when account does not exist")
    void shouldThrowWhenAccountNotFound() {
        AccountId nonExistentId = AccountId.newId();
        when(accountRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        WithdrawMoneyCommand command = new WithdrawMoneyCommand(nonExistentId.value(), 50.00);

        assertThrows(AccountNotFoundException.class, () -> service.withdrawMoney(command));
        verify(accountRepository, never()).save(any());
    }
}
