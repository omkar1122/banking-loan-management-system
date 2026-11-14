package com.bank.service;

import com.bank.exception.BusinessRuleException;
import com.bank.model.Account;
import com.bank.model.AccountType;
import com.bank.repository.AccountRepository;
import com.bank.repository.BankTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/** Business-rule unit tests: minimum balance, daily limit, transfer between different accounts. */
@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
    @Mock AccountRepository accounts;
    @Mock BankTransactionRepository txns;
    @InjectMocks TransactionService service;

    Account acc;

    @BeforeEach
    void setUp() {
        AccountType type = new AccountType();
        type.setName("SAVINGS");
        type.setMinBalance(new BigDecimal("1000"));
        type.setDailyWithdrawLimit(new BigDecimal("50000"));
        acc = new Account();
        acc.setId(1L);
        acc.setAccountNumber("AC1");
        acc.setAccountType(type);
        acc.setBalance(new BigDecimal("1500"));
        acc.setStatus(Account.Status.ACTIVE);
    }

    @Test
    void withdrawBelowMinimumBalanceIsRejectedAndBalanceUnchanged() {
        when(accounts.findForUpdate("AC1")).thenReturn(Optional.of(acc));

        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> service.withdraw("AC1", new BigDecimal("600"), null));

        assertTrue(ex.getMessage().contains("Minimum balance"));
        assertEquals(new BigDecimal("1500"), acc.getBalance());
        verify(txns, never()).save(any());
    }

    @Test
    void withdrawLeavingExactlyMinimumBalanceIsAllowed() {
        when(accounts.findForUpdate("AC1")).thenReturn(Optional.of(acc));

        service.withdraw("AC1", new BigDecimal("500"), null);

        assertEquals(new BigDecimal("1000"), acc.getBalance());
        verify(txns, times(1)).save(any());
    }

    @Test
    void dailyWithdrawalLimitIsEnforced() {
        acc.setBalance(new BigDecimal("500000"));
        when(accounts.findForUpdate("AC1")).thenReturn(Optional.of(acc));
        when(txns.sumAmount(eq(1L), any(), any(), any())).thenReturn(new BigDecimal("49000"));

        assertThrows(BusinessRuleException.class, () -> service.withdraw("AC1", new BigDecimal("2000"), null));
        assertEquals(new BigDecimal("500000"), acc.getBalance());
    }

    @Test
    void transferToSameAccountIsRejected() {
        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> service.transfer("AC1", "AC1", new BigDecimal("100"), null));
        assertTrue(ex.getMessage().contains("different"));
        verifyNoInteractions(accounts);
    }

    @Test
    void nonPositiveAmountIsRejected() {
        assertThrows(BusinessRuleException.class, () -> service.deposit("AC1", BigDecimal.ZERO, null));
    }

    @Test
    void frozenAccountCannotBeDebited() {
        acc.setStatus(Account.Status.FROZEN);
        when(accounts.findForUpdate("AC1")).thenReturn(Optional.of(acc));
        assertThrows(BusinessRuleException.class, () -> service.withdraw("AC1", new BigDecimal("100"), null));
    }
}
