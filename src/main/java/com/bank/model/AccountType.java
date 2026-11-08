package com.bank.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "account_type")
@Getter @Setter
public class AccountType {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String name;                       // SAVINGS, CURRENT
    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal minBalance;             // minimum-balance rule
    @Column(precision = 5, scale = 2)
    private BigDecimal interestRate;
    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal dailyWithdrawLimit;     // daily withdrawal limit rule
}
