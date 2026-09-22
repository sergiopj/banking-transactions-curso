package com.banking.transactions.application.dto;

import java.util.List;

/**
 * DTO DE SALIDA DE APLICACIÓN - Output DTO / Query Result
 *
 * PATH: application/dto/ — Va aquí y no en infrastructure/ porque es el
 * resultado que devuelve el UseCase (ej: GetAccountDetailsUseCase).
 * Es Java puro, sin @JsonProperty. El Controller luego lo convierte a Response.
 *
 * POR QUÉ public record y no class:
 * - record (Java 16+) es inmutable por diseño, perfecto para un DTO de salida.
 * - Te genera automáticamente constructor, getters (id(), customerId(), etc.),
 * equals, hashCode y toString sin boilerplate.
 *
 * POR QUÉ public:
 * - Lo necesita tanto el UseCase que lo crea como el Controller que lo
 * devuelve.
 *
 * OJO BANCA: balance debería ser BigDecimal o String. double está aquí por
 * simplicidad del curso.
 */
public record AccountDetailsDto(
                String id,
                String customerId,
                double balance,
                List<TransactionDto> transactions) {
}