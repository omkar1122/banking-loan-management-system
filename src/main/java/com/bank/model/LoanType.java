package com.bank.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "loan_type")
@Getter @Setter
public class LoanType {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Column(unique = true)
    private String name;

    @NotNull(message = "Interest rate is required")
    @DecimalMin(value = "0.0", message = "Rate cannot be negative")
    @DecimalMax(value = "40.0", message = "Rate cannot exceed 40%")
    @Column(precision = 5, scale = 2)
    private BigDecimal interestRate;

    @NotNull(message = "Maximum tenure is required")
    @Min(value = 1, message = "Tenure must be at least 1 month")
    @Max(value = 480, message = "Tenure cannot exceed 480 months")
    private Integer maxTenureMonths;

    /** Rule: loan amount must not exceed incomeMultiple x customer's monthly income. */
    @NotNull(message = "Income multiple is required")
    @DecimalMin(value = "1.0", message = "Multiple must be at least 1")
    @Column(precision = 6, scale = 2)
    private BigDecimal incomeMultiple;
}
