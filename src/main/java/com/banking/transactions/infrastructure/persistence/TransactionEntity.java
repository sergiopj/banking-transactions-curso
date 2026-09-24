package com.banking.transactions.infrastructure.persistence;

import java.math.BigDecimal;
import com.banking.transactions.domain.model.TransactionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * CAPA: Infrastructure / Persistence
 * 
 * Esta NO es la entidad de dominio (la del Aggregate).
 * Esta es la entidad de persistencia, solo sirve para mapear la tabla MySQL.
 * 
 * El dominio puro está en: domain/model/Transaction.java
 * Esta de aquí es su reflejo en BD y vive fuera del corazón de la hexagonal.
 * 
 * Representa el historial de movimientos de una cuenta. Es la Entidad Hija
 * del Aggregate cuyo Root es Account.
 */
@Entity // Le dice a JPA/Hibernate: esta clase es una tabla
@Table(name = "transactions") // Nombre real de la tabla en MySQL
@Setter // Lombok genera setters para JPA, aunque no los usemos en el dominio
@Getter // Lombok genera getters para JPA, aunque no los usemos en el dominio
public class TransactionEntity {

    /**
     * Identidad de la transacción.
     * Es el PK de la tabla. Lo ponemos como String porque en el dominio
     * usamos UUID para no depender de la BD.
     * 
     * Aunque es private, JPA lo lee por reflexión con Field Access.
     */
    @Id
    private String id;

    /**
     * Tipo de movimiento. DEPOSIT / WITHDRAWAL.
     * 
     * NOTA DE DDD: Idealmente aquí no importaríamos TransactionType del dominio
     * para no acoplar infrastructure a domain. Lo normal es guardarlo como String
     * y mapearlo en el Adapter. Para el curso si os dejan importarlo, vale.
     * 
     * nullable = false -> invariante de BD: toda transacción debe tener tipo.
     */
    @Column(nullable = false)
    private TransactionType type;

    /**
     * Importe del movimiento.
     * Usamos BigDecimal y no double por precisión monetaria.
     * precision = 19, scale = 2 -> formato SQL DECIMAL(19,2), estándar para dinero.
     * Guarda hasta 999 billones con 2 decimales, evita errores de redondeo.
     * 
     * Este dato + el balance de AccountEntity deben cambiar de forma conjunta.
     * Es el límite de consistencia que vimos: si falla el balance, no se guarda
     * esto.
     */
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    // TODO para completar el Aggregate:
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "account_id", nullable = false)
    // private AccountEntity account; // Vinculación a su Root Aggregate

    /**
     * Constructor vacío requerido por JPA.
     * Hibernate necesita crearlo por reflexión. Lo ponemos protected
     * para que nadie lo use desde el código de negocio.
     */
    protected TransactionEntity() {
    }
}