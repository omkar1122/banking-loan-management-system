package com.bank.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class AccountForm {
    private Long id;                         // null when creating
    @NotNull(message = "Branch is required")
    private Long branchId;
    @NotNull(message = "Account type is required")
    private Long accountTypeId;
    @NotEmpty(message = "Select at least one account holder")
    private List<Long> holderIds = new ArrayList<>();
    /** Only used on create: must be at least the account type's minimum balance. */
    @DecimalMin(value = "0.00", message = "Opening deposit cannot be negative")
    private BigDecimal initialDeposit = BigDecimal.ZERO;
    private String status = "ACTIVE";
}
