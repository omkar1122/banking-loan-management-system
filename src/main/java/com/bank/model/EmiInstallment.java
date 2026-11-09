package com.bank.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "emi_installment")
@Getter @Setter
public class EmiInstallment {
    public enum Status { PENDING, PAID }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "loan_id")
    private Loan loan;

    @Column(nullable = false)
    private Integer installmentNo;
    @Column(nullable = false)
    private LocalDate dueDate;
    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal emiAmount;
    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal principalComponent;
    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal interestComponent;
    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal balanceOutstanding;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Status status = Status.PENDING;
    private LocalDate paidOn;
}
