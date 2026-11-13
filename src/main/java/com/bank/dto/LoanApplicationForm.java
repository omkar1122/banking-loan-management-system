package com.bank.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter @Setter
public class LoanApplicationForm {
    @NotNull(message = "Customer is required")
    private Long customerId;
    @NotNull(message = "Loan type is required")
    private Long loanTypeId;
    @NotNull(message = "Disbursement account is required")
    private Long accountId;
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1000.00", message = "Minimum loan amount is 1000")
    private BigDecimal amount;
    @NotNull(message = "Tenure is required")
    @Min(value = 1, message = "Tenure must be at least 1 month")
    private Integer tenureMonths;
}
