package com.banking.transactions.domain.model;

/**
 * VALUE OBJECT / ENUM DE DOMINIO - Tipo de movimiento bancario
 * 
 * PATH: domain/model/ -> Va aquí porque es lenguaje del negocio puro,
 * no depende de Spring ni de base de datos. Es parte del Aggregate Account.
 * Lo usa Transaction para saber qué hizo: ¿entró o salió dinero?
 * 
 * POR QUÉ public enum y no String: Type safety. Si usaras String te pueden
 * pasar "deposito", "DEPOSITO", "deposit"... Con enum solo existen 2 valores
 * válidos y el compilador te protege. Evitas bugs en banca.
 * 
 * POR QUÉ TransactionType: Lenguaje ubicuo DDD. En el banco se habla de
 * tipo de transacción. No es TransactionTypeDTO ni TransactionTypeEntity,
 * es el tipo del dominio.
 * 
 * TODO que dejas: Buena visión. Ahora mismo es un enum simple, pero a futuro
 * tendría sentido que sea una entidad fuerte (ej. tabla transaction_types)
 * si cada tipo tiene reglas, comisiones, límites, o contabilidad distinta.
 * Ej: WITHDRAW con comisión, DEPOSIT con validación AML. Ahí ya no te sirve un
 * enum.
 */
public enum TransactionType {
    DEPOSIT, // Entra dinero -> account.deposit() lo crea, suma al balance
    WITHDRAW // Sale dinero -> account.withdraw() lo crea, resta y valida saldo
}