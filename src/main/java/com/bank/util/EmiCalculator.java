package com.bank.util;

import com.bank.model.EmiInstallment;
import com.bank.model.Loan;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** EMI = P * r * (1+r)^n / ((1+r)^n - 1), r = annual rate / 12 / 100. */
public final class EmiCalculator {
    private static final MathContext MC = new MathContext(20, RoundingMode.HALF_UP);
    private EmiCalculator() {}

    public static BigDecimal emi(BigDecimal principal, BigDecimal annualRatePercent, int months) {
        if (months <= 0) throw new IllegalArgumentException("months must be positive");
        if (annualRatePercent.signum() == 0) {
            return principal.divide(BigDecimal.valueOf(months), 2, RoundingMode.HALF_UP);
        }
        BigDecimal r = annualRatePercent.divide(BigDecimal.valueOf(1200), MC);
        BigDecimal pow = BigDecimal.ONE.add(r).pow(months, MC);
        return principal.multiply(r).multiply(pow).divide(pow.subtract(BigDecimal.ONE), MC)
                .setScale(2, RoundingMode.HALF_UP);
    }

    /** Builds the full amortisation schedule; the last instalment clears any rounding difference. */
    public static List<EmiInstallment> schedule(Loan loan, LocalDate firstDueDate) {
        List<EmiInstallment> list = new ArrayList<>();
        BigDecimal r = loan.getAnnualRate().divide(BigDecimal.valueOf(1200), MC);
        BigDecimal balance = loan.getPrincipal();
        for (int i = 1; i <= loan.getTenureMonths(); i++) {
            BigDecimal interest = balance.multiply(r).setScale(2, RoundingMode.HALF_UP);
            BigDecimal principalPart = loan.getEmiAmount().subtract(interest);
            if (i == loan.getTenureMonths() || principalPart.compareTo(balance) > 0) principalPart = balance;
            balance = balance.subtract(principalPart);
            EmiInstallment e = new EmiInstallment();
            e.setLoan(loan);
            e.setInstallmentNo(i);
            e.setDueDate(firstDueDate.plusMonths(i - 1L));
            e.setPrincipalComponent(principalPart);
            e.setInterestComponent(interest);
            e.setEmiAmount(principalPart.add(interest));
            e.setBalanceOutstanding(balance);
            e.setStatus(EmiInstallment.Status.PENDING);
            list.add(e);
        }
        return list;
    }
}
