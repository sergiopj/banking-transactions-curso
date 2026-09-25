package com.banking.transactions.application.service;

import com.banking.transactions.application.dto.AccountDetailsDto;
import com.banking.transactions.application.dto.CreateAccountCommand;
import com.banking.transactions.domain.model.Account;
import com.banking.transactions.domain.port.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateAccountService Unit Tests")
class CreateAccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    private CreateAccountService service;

    @BeforeEach
    void setUp() {
        service = new CreateAccountService(accountRepository);
    }

    @Test
    @DisplayName("Should create account with initial balance and save to repository")
    void shouldCreateAccountWithInitialBalance() {
        CreateAccountCommand command = new CreateAccountCommand("cust-100", 150.50);

        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AccountDetailsDto result = service.createAccount(command);

        assertNotNull(result);
        assertEquals("cust-100", result.customerId());
        assertEquals(150.50, result.balance());

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository, times(1)).save(captor.capture());

        Account saved = captor.getValue();
        assertEquals("cust-100", saved.getCustomerId());
        assertEquals(150.50, saved.getBalance().getAmount().doubleValue());
    }

    @Test
    @DisplayName("Should default to zero balance when initialBalance is null")
    void shouldCreateAccountWithZeroBalanceWhenNull() {
        CreateAccountCommand command = new CreateAccountCommand("cust-200", null);

        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AccountDetailsDto result = service.createAccount(command);

        assertNotNull(result);
        assertEquals(0.00, result.balance());
    }
}
