package com.banking.transactions.infrastructure.persistence;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Infraestructura / Persistencia - Reflejo en BD del Root Aggregate Account.
 * Sin lógica de negocio, solo mapeo JPA. El dominio puro está en
 * domain/model/Account.java
 */
@Entity
@Table(name = "accounts")
@Getter
@Setter // Lombok para JPA, el dominio sigue sin setters
@NoArgsConstructor
public class AccountEntity {

    /** PK de la tabla. */
    @Id
    private String id;

    /** Propietario de la cuenta. FK lógica a customers, obligatorio. */
    @Column(nullable = false)
    private String customerId;

    /**
     * Saldo actual. DECIMAL(19,2) para dinero. Obligatorio, es parte del límite de
     * consistencia.
     */
    @Column(precision = 19, scale = 2, nullable = false)
    private BigDecimal balance;

    /**
     * Entidades Hijas del Aggregate.
     * mappedBy = "account" -> el atributo en TransactionEntity que apunta aquí.
     * CASCADE ALL -> al guardar Account se guardan sus transacciones (cambian
     * juntas).
     * orphanRemoval = true -> si quitas una transacción de la lista, se borra de
     * BD.
     * Inicializada para evitar NullPointer.
     */
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransactionEntity> transactions = new ArrayList<>();

    /**
     * Helper para mantener la relación bidireccional. Sincroniza ambos lados:
     * Transaction -> Account y Account -> Transactions
     */
    public void addTransaction(TransactionEntity transactionEntity) {
        transactionEntity.setAccount(this);
        this.transactions.add(transactionEntity);
    }

}