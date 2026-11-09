package com.bank.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "loan")
@Getter @Setter
public class Loan {
    public enum Status { ACTIVE, CLOSED }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** One application can produce at most one loan (unique FK guards against double approval). */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "application_id", unique = true)
    private LoanApplication application;

    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "customer_id")
    private Customer customer;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "loan_type_id")
    private LoanType loanType;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "account_id")
    private Account account;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal principal;
    @Column(precision = 5, scale = 2, nullable = false)
    private BigDecimal annualRate;
    @Column(nullable = false)
    private Integer tenureMonths;
    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal emiAmount;
    private LocalDate disbursedOn;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Status status = Status.ACTIVE;

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL)
    @OrderBy("installmentNo ASC")
    private List<EmiInstallment> installments = new ArrayList<>();
}
