package com.banking.transactions.application.dto;

import java.util.List;

/**
 * Output DTO de Application. Lo crea el UseCase y lo lee el Controller.
 * Va en application/dto y no en infrastructure porque es resultado del caso de
 * uso.
 * Es un record inmutable (Java 16+) -> sin boilerplate. Sin @JsonProperty.
 * Balance debería ser BigDecimal, aquí double por simplicidad del curso.
 */
public record AccountDetailsDto(
        String id,
        String customerId,
        double balance,
        List<TransactionDto> transactions) {
}