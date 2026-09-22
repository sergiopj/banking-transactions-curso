package com.banking.transactions.application.dto;

/**
 * COMMAND DE ENTRADA - Input DTO / Write Model (CQRS)
 * 
 * PATH: application/dto/ -> Va aquí porque es lo que NECESITA el UseCase
 * WithdrawMoneyUseCase para trabajar. No va en infrastructure/web/ porque
 * el Core no debe depender de Spring.
 * 
 * POR QUÉ public record y no class: Command inmutable. Una vez que el usuario
 * dice "quiero retirar 100 de la cuenta X", ese deseo no cambia en mitad del
 * flujo. record te da inmutabilidad + constructor + getters gratis.
 * 
 * POR QUÉ WithdrawMoneyCommand y no WithdrawRequest: Semántica CQRS.
 * Request es palabra de HTTP (infrastructure). Command es palabra de Dominio
 * y Aplicación. Dice "ordena hacer algo". Deja claro que es escritura, no
 * lectura.
 * 
 * POR QUÉ String accountId y no AccountId: En el borde de la aplicación entra
 * como String desde el JSON. El UseCase luego hace AccountId.of(accountId)
 * y valida. Así no metes Value Objects del dominio en el DTO.
 * 
 * POR QUÉ double amount: Mismo truco del curso que en los otros DTOs. Entra
 * como
 * double por JSON y el UseCase lo convierte a Money.of() o Money.from().
 * En el dominio nunca se usa double, solo Money con BigDecimal.
 */
public record WithdrawMoneyCommand(
        String accountId,
        double amount) {
}