package com.banking.transactions.infrastructure.web.dto;

import java.time.Instant;
import java.util.List;

// DTO para envolver el mensaje de error de una excepcion
// Se usa desde el GlobalExceptionHandler para devolver un JSON limpio al cliente
public record ErrorMessage(
        int status, // codigo http: 400, 404, 409, 500 // TODO tipo enum?
        String error, // tipo de error: Bad Request, Not Found, Conflict
        String message, // mensaje principal en ingles para el cliente
        String path, // endpoint donde fallo: /api/accounts/{id}
        Instant timestamp, // instante UTC cuando ocurrio el error
        List<String> details // lista de detalles, ej: errores de @Valid field by field
) {
}