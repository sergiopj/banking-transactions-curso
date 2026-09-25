package com.banking.transactions.application.service;

import com.banking.transactions.application.dto.AccountDetailsDto;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetAccountDetailsService Unit Tests")
class GetAccountDetailsServiceTest {

    @Mock
    private AccountRepository accountRepository;

    private GetAccountDetailsService service;

    @BeforeEach
    void setUp() {
        service = new GetAccountDetailsService(accountRepository);
    }

    @Test
    @DisplayName("Should return account details DTO when account exists")
    void shouldReturnAccountDetailsWhenFound() {
        AccountId id = AccountId.newId();
        Account account = new Account(id, "cust-1", Money.of("300.00"));

        when(accountRepository.findById(id)).thenReturn(Optional.of(account));

        AccountDetailsDto dto = service.getById(id.value());

        assertNotNull(dto);
        assertEquals(id.value(), dto.id());
        assertEquals("cust-1", dto.customerId());
        assertEquals(300.00, dto.balance());
    }

    @Test
    @DisplayName("Should throw AccountNotFoundException when account is not found")
    void shouldThrowWhenNotFound() {
        AccountId id = AccountId.newId();
        when(accountRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> service.getById(id.value()));
    }
}
