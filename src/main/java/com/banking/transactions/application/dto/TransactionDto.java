package com.banking.transactions.application.dto;

// DTO representing a single transaction in the account details response
public record TransactionDto(
                String id,
                String transactionType,
                double amount) {
}
