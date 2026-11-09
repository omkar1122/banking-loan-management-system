package com.bank.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "loan_application")
@Getter @Setter
public class LoanApplication {
    public enum Status { PENDING, APPROVED, REJECTED }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "customer_id")
    private Customer customer;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "loan_type_id")
    private LoanType loanType;
    /** Account that receives the disbursement and is debited for EMIs. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "account_id")
    private Account account;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal amount;
    @Column(nullable = false)
    private Integer tenureMonths;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Status status = Status.PENDING;

    private LocalDate appliedOn;
    private LocalDate decidedOn;
    private String remarks;
}
