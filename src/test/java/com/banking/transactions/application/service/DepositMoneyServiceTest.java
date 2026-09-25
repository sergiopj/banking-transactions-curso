package com.banking.transactions.application.service;

import com.banking.transactions.application.dto.AccountDetailsDto;
import com.banking.transactions.application.dto.DepositMoneyCommand;
import com.banking.transactions.domain.exception.AccountNotFoundException;
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
@DisplayName("DepositMoneyService Unit Tests")
class DepositMoneyServiceTest {

    @Mock
    private AccountRepository accountRepository;

    private DepositMoneyService service;

    @BeforeEach
    void setUp() {
        service = new DepositMoneyService(accountRepository);
    }

    @Test
    @DisplayName("Should successfully deposit money into an existing account")
    void shouldDepositMoneySuccessfully() {
        AccountId accountId = AccountId.newId();
        Account existingAccount = new Account(accountId, "cust-1", Money.of("100.00"));

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(existingAccount));
        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));

        DepositMoneyCommand command = new DepositMoneyCommand(accountId.value(), 50.00);
        AccountDetailsDto result = service.depositMoney(command);

        assertNotNull(result);
        assertEquals(150.00, result.balance());
        assertEquals(1, result.transactions().size());

        verify(accountRepository, times(1)).save(existingAccount);
    }

    @Test
    @DisplayName("Should throw AccountNotFoundException when account does not exist")
    void shouldThrowWhenAccountNotFound() {
        AccountId nonExistentId = AccountId.newId();
        when(accountRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        DepositMoneyCommand command = new DepositMoneyCommand(nonExistentId.value(), 50.00);

        assertThrows(AccountNotFoundException.class, () -> service.depositMoney(command));
        verify(accountRepository, never()).save(any());
    }
}
