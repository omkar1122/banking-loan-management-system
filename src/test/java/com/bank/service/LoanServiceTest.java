package com.bank.service;

import com.bank.dto.LoanApplicationForm;
import com.bank.exception.BusinessRuleException;
import com.bank.model.*;
import com.bank.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/** Business-rule unit tests: only PENDING can be decided; loan amount capped by income; tenure cap. */
@ExtendWith(MockitoExtension.class)
class LoanServiceTest {
    @Mock LoanApplicationRepository applications;
    @Mock LoanRepository loans;
    @Mock EmiInstallmentRepository emis;
    @Mock CustomerRepository customers;
    @Mock LoanTypeRepository loanTypes;
    @Mock AccountRepository accounts;
    @Mock TransactionService txnService;
    @InjectMocks LoanService service;

    @Test
    void approvingAnAlreadyApprovedApplicationIsRejected() {
        LoanApplication app = new LoanApplication();
        app.setId(7L);
        app.setStatus(LoanApplication.Status.APPROVED);
        when(applications.findForUpdate(7L)).thenReturn(Optional.of(app));

        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.approve(7L, "again"));

        assertTrue(ex.getMessage().contains("PENDING"));
        verify(loans, never()).save(any());
        verify(txnService, never()).disburseLoan(any(), any(), any());
    }

    @Test
    void rejectingARejectedApplicationIsRejected() {
        LoanApplication app = new LoanApplication();
        app.setStatus(LoanApplication.Status.REJECTED);
        when(applications.findForUpdate(8L)).thenReturn(Optional.of(app));
        assertThrows(BusinessRuleException.class, () -> service.reject(8L, "x"));
    }

    @Test
    void loanAmountAboveIncomeCapIsRejected() {
        Customer c = new Customer();
        c.setId(1L);
        c.setMonthlyIncome(new BigDecimal("30000"));
        LoanType lt = new LoanType();
        lt.setId(1L);
        lt.setName("Personal Loan");
        lt.setIncomeMultiple(new BigDecimal("10"));      // cap = 300000
        lt.setMaxTenureMonths(60);
        lt.setInterestRate(new BigDecimal("11"));
        Account acc = new Account();
        acc.setId(1L);
        acc.setStatus(Account.Status.ACTIVE);
        acc.setHolders(Set.of(c));
        when(customers.findById(1L)).thenReturn(Optional.of(c));
        when(loanTypes.findById(1L)).thenReturn(Optional.of(lt));
        when(accounts.findById(1L)).thenReturn(Optional.of(acc));

        LoanApplicationForm f = new LoanApplicationForm();
        f.setCustomerId(1L); f.setLoanTypeId(1L); f.setAccountId(1L);
        f.setAmount(new BigDecimal("300001")); f.setTenureMonths(24);

        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.apply(f));
        assertTrue(ex.getMessage().contains("eligible limit"));
        verify(applications, never()).save(any());
    }
}
