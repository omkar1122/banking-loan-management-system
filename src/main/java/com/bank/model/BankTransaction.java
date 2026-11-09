package com.bank.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Ledger row: every change of an account balance has exactly one BankTransaction. */
@Entity
@Table(name = "bank_transaction")
@Getter @Setter
public class BankTransaction {
    public enum Type { DEPOSIT, WITHDRAWAL, TRANSFER_IN, TRANSFER_OUT, LOAN_DISBURSAL, EMI_PAYMENT }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id")
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Type type;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal balanceAfter;

    @Column(nullable = false)
    private LocalDateTime txnTime;

    private String description;

    /** Same reference on both legs of a transfer. */
    @Column(length = 20)
    private String referenceNo;
}
