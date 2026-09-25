package com.banking.transactions.infrastructure.persistence;

import java.math.BigDecimal;
import java.time.Instant;

import com.banking.transactions.domain.model.TransactionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Infraestructura / Persistencia - NO es el dominio.
 * Mapea la tabla MySQL `transactions`. Es la Entidad Hija del Aggregate,
 * el Root es Account. El modelo puro está en domain/model/Transaction.java
 */
@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
public class TransactionEntity {

    /**
     * PK de la tabla. UUID en String para no atarnos a la BD. Private pero JPA lo
     * lee por reflexión.
     */
    @Id
    private String id;

    /**
     * DEPOSIT / WITHDRAWAL. Se persiste como STRING en BD (VARCHAR) para
     * legibilidad y estabilidad.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    /**
     * Importe con BigDecimal (no double) para precisión monetaria. DECIMAL(19,2).
     */
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    // Columna de auditoría. Instant en UTC para banca, no LocalDateTime
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /**
     * Relación a su Root Aggregate. LAZY = no carga Account hasta usarla. FK
     * account_id obligatoria.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private AccountEntity account;

}

// TODO se puede vitaminar esto como method por ejemplo app, cajero, oficina etc
