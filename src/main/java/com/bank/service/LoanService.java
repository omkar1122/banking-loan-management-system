package com.bank.service;

import com.bank.dto.LoanApplicationForm;
import com.bank.exception.BusinessRuleException;
import com.bank.exception.ResourceNotFoundException;
import com.bank.model.*;
import com.bank.repository.*;
import com.bank.util.EmiCalculator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class LoanService {
    private final LoanApplicationRepository applications;
    private final LoanRepository loans;
    private final EmiInstallmentRepository emis;
    private final CustomerRepository customers;
    private final LoanTypeRepository loanTypes;
    private final AccountRepository accounts;
    private final TransactionService txnService;

    public LoanService(LoanApplicationRepository applications, LoanRepository loans, EmiInstallmentRepository emis,
                       CustomerRepository customers, LoanTypeRepository loanTypes, AccountRepository accounts,
                       TransactionService txnService) {
        this.applications = applications; this.loans = loans; this.emis = emis;
        this.customers = customers; this.loanTypes = loanTypes; this.accounts = accounts; this.txnService = txnService;
    }

    /** Rules: account must belong to applicant and be ACTIVE; amount <= incomeMultiple x monthly income; tenure <= type max. */
    @Transactional
    public LoanApplication apply(LoanApplicationForm f) {
        Customer c = customers.findById(f.getCustomerId()).orElseThrow(() -> new ResourceNotFoundException("Customer not found."));
        LoanType lt = loanTypes.findById(f.getLoanTypeId()).orElseThrow(() -> new ResourceNotFoundException("Loan type not found."));
        Account acc = accounts.findById(f.getAccountId()).orElseThrow(() -> new ResourceNotFoundException("Account not found."));

        if (acc.getHolders().stream().noneMatch(h -> h.getId().equals(c.getId()))) {
            throw new BusinessRuleException("The selected account does not belong to this customer.");
        }
        if (acc.getStatus() != Account.Status.ACTIVE) {
            throw new BusinessRuleException("The disbursement account must be ACTIVE.");
        }
        checkEligibility(c, lt, f.getAmount(), f.getTenureMonths());

        LoanApplication app = new LoanApplication();
        app.setCustomer(c); app.setLoanType(lt); app.setAccount(acc);
        app.setAmount(f.getAmount()); app.setTenureMonths(f.getTenureMonths());
        app.setStatus(LoanApplication.Status.PENDING);
        app.setAppliedOn(LocalDate.now());
        return applications.save(app);
    }

    void checkEligibility(Customer c, LoanType lt, BigDecimal amount, int tenure) {
        BigDecimal cap = c.getMonthlyIncome().multiply(lt.getIncomeMultiple());
        if (amount.compareTo(cap) > 0) {
            throw new BusinessRuleException("Loan amount " + amount + " exceeds the eligible limit of " + cap
                    + " (" + lt.getIncomeMultiple() + " x monthly income).");
        }
        if (tenure > lt.getMaxTenureMonths()) {
            throw new BusinessRuleException("Tenure cannot exceed " + lt.getMaxTenureMonths() + " months for " + lt.getName() + ".");
        }
    }

    /**
     * Approval = status change + Loan row + full EMI schedule + disbursement credit + ledger row.
     * One transaction: any failure leaves the application PENDING and no money moved.
     */
    @Transactional
    public Loan approve(Long applicationId, String remarks) {
        LoanApplication app = applications.findForUpdate(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application #" + applicationId + " was not found."));
        if (app.getStatus() != LoanApplication.Status.PENDING) {
            throw new BusinessRuleException("Only PENDING applications can be approved. This one is already " + app.getStatus() + ".");
        }
        LoanType lt = app.getLoanType();
        Loan loan = new Loan();
        loan.setApplication(app);
        loan.setCustomer(app.getCustomer());
        loan.setLoanType(lt);
        loan.setAccount(app.getAccount());
        loan.setPrincipal(app.getAmount());
        loan.setAnnualRate(lt.getInterestRate());
        loan.setTenureMonths(app.getTenureMonths());
        loan.setEmiAmount(EmiCalculator.emi(app.getAmount(), lt.getInterestRate(), app.getTenureMonths()));
        loan.setDisbursedOn(LocalDate.now());
        loan.setStatus(Loan.Status.ACTIVE);
        loan.getInstallments().addAll(EmiCalculator.schedule(loan, LocalDate.now().plusMonths(1)));
        loan = loans.save(loan);                       // cascades the instalments

        txnService.disburseLoan(app.getAccount().getAccountNumber(), app.getAmount(), "Loan disbursal, loan #" + loan.getId());

        app.setStatus(LoanApplication.Status.APPROVED);
        app.setDecidedOn(LocalDate.now());
        app.setRemarks(remarks);
        applications.save(app);
        return loan;
    }

    @Transactional
    public LoanApplication reject(Long applicationId, String remarks) {
        LoanApplication app = applications.findForUpdate(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application #" + applicationId + " was not found."));
        if (app.getStatus() != LoanApplication.Status.PENDING) {
            throw new BusinessRuleException("Only PENDING applications can be rejected. This one is already " + app.getStatus() + ".");
        }
        if (remarks == null || remarks.isBlank()) throw new BusinessRuleException("Please give a reason for rejection.");
        app.setStatus(LoanApplication.Status.REJECTED);
        app.setDecidedOn(LocalDate.now());
        app.setRemarks(remarks.trim());
        return applications.save(app);
    }

    /** Pays the oldest unpaid instalment from the loan account (debit + ledger + instalment status, atomically). */
    @Transactional
    public EmiInstallment payNextEmi(Long loanId) {
        Loan loan = loans.findById(loanId).orElseThrow(() -> new ResourceNotFoundException("Loan #" + loanId + " was not found."));
        EmiInstallment e = emis.findFirstByLoanIdAndStatusOrderByInstallmentNo(loanId, EmiInstallment.Status.PENDING)
                .orElseThrow(() -> new BusinessRuleException("All instalments of this loan are already paid."));
        txnService.payEmi(loan.getAccount().getAccountNumber(), e.getEmiAmount(),
                "EMI " + e.getInstallmentNo() + "/" + loan.getTenureMonths() + " for loan #" + loan.getId());
        e.setStatus(EmiInstallment.Status.PAID);
        e.setPaidOn(LocalDate.now());
        emis.save(e);
        if (emis.findFirstByLoanIdAndStatusOrderByInstallmentNo(loanId, EmiInstallment.Status.PENDING).isEmpty()) {
            loan.setStatus(Loan.Status.CLOSED);
            loans.save(loan);
        }
        return e;
    }
}
