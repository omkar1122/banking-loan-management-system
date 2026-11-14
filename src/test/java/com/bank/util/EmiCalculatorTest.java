package com.bank.util;

import com.bank.model.EmiInstallment;
import com.bank.model.Loan;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/** Business rule: EMI is computed with the standard reducing-balance formula. */
class EmiCalculatorTest {

    @Test
    void emiMatchesStandardFormula() {
        // 100000 at 12% p.a. for 12 months -> 8884.88 (standard reference value)
        BigDecimal emi = EmiCalculator.emi(new BigDecimal("100000"), new BigDecimal("12"), 12);
        assertEquals(new BigDecimal("8884.88"), emi);
    }

    @Test
    void zeroInterestSplitsPrincipalEvenly() {
        assertEquals(new BigDecimal("1000.00"), EmiCalculator.emi(new BigDecimal("12000"), BigDecimal.ZERO, 12));
    }

    @Test
    void scheduleRepaysPrincipalExactly() {
        Loan loan = new Loan();
        loan.setPrincipal(new BigDecimal("100000"));
        loan.setAnnualRate(new BigDecimal("12"));
        loan.setTenureMonths(12);
        loan.setEmiAmount(EmiCalculator.emi(loan.getPrincipal(), loan.getAnnualRate(), 12));

        List<EmiInstallment> s = EmiCalculator.schedule(loan, LocalDate.of(2026, 1, 1));

        assertEquals(12, s.size());
        BigDecimal principalSum = s.stream().map(EmiInstallment::getPrincipalComponent).reduce(BigDecimal.ZERO, BigDecimal::add);
        assertEquals(0, loan.getPrincipal().compareTo(principalSum));
        assertEquals(0, BigDecimal.ZERO.compareTo(s.get(11).getBalanceOutstanding()));
        assertEquals(LocalDate.of(2026, 12, 1), s.get(11).getDueDate());
    }
}
