package com.bank.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter @Setter
public class CashForm {
    @NotBlank(message = "Account number is required")
    private String accountNumber;
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    @Digits(integer = 13, fraction = 2, message = "Amount can have at most 2 decimals")
    private BigDecimal amount;
    @Size(max = 100)
    private String description;
}
