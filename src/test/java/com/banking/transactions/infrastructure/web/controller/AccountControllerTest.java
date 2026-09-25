package com.banking.transactions.infrastructure.web.controller;

import com.banking.transactions.application.dto.AccountDetailsDto;
import com.banking.transactions.application.dto.CreateAccountCommand;
import com.banking.transactions.application.dto.DepositMoneyCommand;
import com.banking.transactions.application.dto.TransactionDto;
import com.banking.transactions.application.dto.WithdrawMoneyCommand;
import com.banking.transactions.application.port.CreateAccountUseCase;
import com.banking.transactions.application.port.DepositMoneyUseCase;
import com.banking.transactions.application.port.GetAccountDetailsUseCase;
import com.banking.transactions.application.port.WithdrawMoneyUseCase;
import com.banking.transactions.domain.exception.AccountNotFoundException;
import com.banking.transactions.domain.exception.InsufficientBalanceException;
import com.banking.transactions.domain.model.AccountId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import com.banking.transactions.infrastructure.web.handler.GlobalExceptionHandler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = { AccountController.class, GlobalExceptionHandler.class })
@DisplayName("AccountController WebMvc Integration Tests")
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateAccountUseCase createAccountUseCase;

    @MockitoBean
    private DepositMoneyUseCase depositMoneyUseCase;

    @MockitoBean
    private WithdrawMoneyUseCase withdrawMoneyUseCase;

    @MockitoBean
    private GetAccountDetailsUseCase getAccountDetailsUseCase;

    @Test
    @DisplayName("POST /api/accounts should return 201 Created with AccountResponse")
    void shouldCreateAccountSuccessfully() throws Exception {
        AccountDetailsDto dto = new AccountDetailsDto("acc-1", "cust-1", 100.00, List.of());
        when(createAccountUseCase.createAccount(any(CreateAccountCommand.class))).thenReturn(dto);

        String payload = """
                {
                    "customerId": "cust-1",
                    "initialBalance": 100.00
                }
                """;

        mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("acc-1"))
                .andExpect(jsonPath("$.customerId").value("cust-1"))
                .andExpect(jsonPath("$.balance").value(100.00));
    }

    @Test
    @DisplayName("POST /api/accounts should return 400 Bad Request when customerId is blank")
    void shouldFailCreatingAccountWhenCustomerIdBlank() throws Exception {
        String payload = """
                {
                    "customerId": "",
                    "initialBalance": 100.00
                }
                """;

        mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/accounts/{id}/deposit should return 200 OK")
    void shouldDepositSuccessfully() throws Exception {
        TransactionDto txDto = new TransactionDto("tx-1", "DEPOSIT", 50.00);
        AccountDetailsDto dto = new AccountDetailsDto("acc-1", "cust-1", 150.00, List.of(txDto));

        when(depositMoneyUseCase.depositMoney(any(DepositMoneyCommand.class))).thenReturn(dto);

        String payload = """
                {
                    "amount": 50.00
                }
                """;

        mockMvc.perform(post("/api/accounts/acc-1/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(150.00))
                .andExpect(jsonPath("$.transactions[0].id").value("tx-1"));
    }

    @Test
    @DisplayName("POST /api/accounts/{id}/withdraw should return 422 Unprocessable Entity when InsufficientBalanceException is thrown")
    void shouldReturn422WhenInsufficientBalance() throws Exception {
        when(withdrawMoneyUseCase.withdrawMoney(any(WithdrawMoneyCommand.class)))
                .thenThrow(new InsufficientBalanceException("Insufficient balance"));

        String payload = """
                {
                    "amount": 500.00
                }
                """;

        mockMvc.perform(post("/api/accounts/acc-1/withdraw")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isUnprocessableContent());
    }

    @Test
    @DisplayName("GET /api/accounts/{id} should return 404 Not Found when AccountNotFoundException is thrown")
    void shouldReturn404WhenAccountNotFound() throws Exception {
        when(getAccountDetailsUseCase.getById("acc-999"))
                .thenThrow(new AccountNotFoundException(new AccountId("acc-999")));

        mockMvc.perform(get("/api/accounts/acc-999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/accounts/{id} should return 200 OK with account details")
    void shouldReturnAccountDetailsSuccessfully() throws Exception {
        AccountDetailsDto dto = new AccountDetailsDto("acc-1", "cust-1", 200.00, List.of());
        when(getAccountDetailsUseCase.getById("acc-1")).thenReturn(dto);

        mockMvc.perform(get("/api/accounts/acc-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("acc-1"))
                .andExpect(jsonPath("$.balance").value(200.00));
    }
}
