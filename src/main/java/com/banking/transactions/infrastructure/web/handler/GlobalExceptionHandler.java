package com.banking.transactions.infrastructure.web.handler;

import java.time.Instant;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.banking.transactions.domain.exception.AccountNotFoundException;
import com.banking.transactions.domain.exception.InsufficientBalanceException;
import com.banking.transactions.domain.exception.NegativeMoneyException;
import com.banking.transactions.infrastructure.web.dto.ErrorMessage;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Intercepta excepciones de los controladores REST y devuelve un JSON
 * formateado
 * con el DTO ErrorMessage estandarizado para toda la API.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

        // 404 - Cuenta no encontrada
        @ExceptionHandler(AccountNotFoundException.class)
        public ResponseEntity<ErrorMessage> handleAccountNotFound(AccountNotFoundException ex,
                        HttpServletRequest request) {
                return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request, List.of());
        }

        // 422 - Saldo insuficiente para realizar el retiro
        @ExceptionHandler(InsufficientBalanceException.class)
        public ResponseEntity<ErrorMessage> handleInsufficientBalance(InsufficientBalanceException ex,
                        HttpServletRequest request) {
                return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), request, List.of());
        }

        // 400 - Importe negativo o cero
        @ExceptionHandler(NegativeMoneyException.class)
        public ResponseEntity<ErrorMessage> handleNegativeMoney(NegativeMoneyException ex, HttpServletRequest request) {
                return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request, List.of());
        }

        // 400 - Errores de validación de Bean Validation (@Valid en DTOs)
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorMessage> handleValidationErrors(MethodArgumentNotValidException ex,
                        HttpServletRequest request) {
                List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                                .toList();

                return buildResponse(HttpStatus.BAD_REQUEST, "Validation failed for request payload", request, errors);
        }

        // 400 - Argumentos ilegales genéricos
        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ErrorMessage> handleIllegalArgument(IllegalArgumentException ex,
                        HttpServletRequest request) {
                return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request, List.of());
        }

        // Método helper privado para evitar duplicar código (DRY)
        private ResponseEntity<ErrorMessage> buildResponse(HttpStatus status, String message,
                        HttpServletRequest request, List<String> details) {
                ErrorMessage errorMessage = new ErrorMessage(
                                status.value(),
                                status.getReasonPhrase(),
                                message,
                                request.getRequestURI(),
                                Instant.now(),
                                details);

                return ResponseEntity.status(status).body(errorMessage);
        }

        // errores 500 genericos
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorMessage> handleGenericError(Exception ex, HttpServletRequest request) {
                return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error", request,
                                List.of(ex.getClass().getSimpleName()));
        }
}
