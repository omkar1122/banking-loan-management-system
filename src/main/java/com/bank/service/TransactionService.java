package com.bank.service;

import com.bank.exception.BusinessRuleException;
import com.bank.exception.ResourceNotFoundException;
import com.bank.model.Account;
import com.bank.model.BankTransaction;
import com.bank.model.BankTransaction.Type;
import com.bank.repository.AccountRepository;
import com.bank.repository.BankTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.UUID;

/**
 * Deposit / withdrawal / transfer. Each public method is one database transaction:
 * if any rule fails the exception rolls back every balance and ledger change.
 */
@Service
public class TransactionService {
    private final AccountRepository accounts;
    private final BankTransactionRepository txns;

    public TransactionService(AccountRepository accounts, BankTransactionRepository txns) {
        this.accounts = accounts;
        this.txns = txns;
    }

    @Transactional
    public BankTransaction deposit(String accountNumber, BigDecimal amount, String description) {
        requirePositive(amount);
        Account a = lock(accountNumber);
        requireActive(a);
        return credit(a, amount, Type.DEPOSIT, orDefault(description, "Cash deposit"), null);
    }

    @Transactional
    public BankTransaction withdraw(String accountNumber, BigDecimal amount, String description) {
        requirePositive(amount);
        Account a = lock(accountNumber);
        requireActive(a);
        checkMinimumBalance(a, amount);
        checkDailyLimit(a, amount);
        return debit(a, amount, Type.WITHDRAWAL, orDefault(description, "Cash withdrawal"), null);
    }

    /** Transfer = debit source + credit destination + two ledger rows, all or nothing. Returns the reference no. */
    @Transactional
    public String transfer(String fromNo, String toNo, BigDecimal amount, String remarks) {
        requirePositive(amount);
        if (fromNo.equals(toNo)) {
            throw new BusinessRuleException("Source and destination accounts must be different.");
        }
        // Lock in a fixed order (by account number) so two opposite transfers cannot deadlock.
        boolean fromFirst = fromNo.compareTo(toNo) < 0;
        Account first = lock(fromFirst ? fromNo : toNo);
        Account second = lock(fromFirst ? toNo : fromNo);
        Account src = fromFirst ? first : second;
        Account dst = fromFirst ? second : first;
        requireActive(src);
        requireActive(dst);
        checkMinimumBalance(src, amount);
        checkDailyLimit(src, amount);

        String ref = "TRF" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        String note = orDefault(remarks, "Fund transfer");
        debit(src, amount, Type.TRANSFER_OUT, note + " to " + dst.getAccountNumber(), ref);
        credit(dst, amount, Type.TRANSFER_IN, note + " from " + src.getAccountNumber(), ref);
        return ref;
    }

    /** Used by LoanService inside its own transaction (propagation REQUIRED joins it). */
    @Transactional
    public BankTransaction disburseLoan(String accountNumber, BigDecimal amount, String description) {
        Account a = lock(accountNumber);
        requireActive(a);
        return credit(a, amount, Type.LOAN_DISBURSAL, description, null);
    }

    /** EMI debit: minimum-balance rule applies, daily withdrawal limit does not. */
    @Transactional
    public BankTransaction payEmi(String accountNumber, BigDecimal amount, String description) {
        Account a = lock(accountNumber);
        requireActive(a);
        checkMinimumBalance(a, amount);
        return debit(a, amount, Type.EMI_PAYMENT, description, null);
    }

    // ---------------- business rules ----------------

    void checkMinimumBalance(Account a, BigDecimal amount) {
        BigDecimal remaining = a.getBalance().subtract(amount);
        BigDecimal min = a.getAccountType().getMinBalance();
        if (remaining.compareTo(min) < 0) {
            throw new BusinessRuleException("Minimum balance rule: account " + a.getAccountNumber()
                    + " must keep at least " + min + ". Balance after this debit would be " + remaining + ".");
        }
    }

    void checkDailyLimit(Account a, BigDecimal amount) {
        LocalDate today = LocalDate.now();
        BigDecimal used = txns.sumAmount(a.getId(), EnumSet.of(Type.WITHDRAWAL, Type.TRANSFER_OUT),
                today.atStartOfDay(), today.plusDays(1).atStartOfDay());
        if (used == null) used = BigDecimal.ZERO;
        BigDecimal limit = a.getAccountType().getDailyWithdrawLimit();
        if (used.add(amount).compareTo(limit) > 0) {
            throw new BusinessRuleException("Daily limit exceeded: limit is " + limit + ", already used " + used + " today.");
        }
    }

    private void requireActive(Account a) {
        if (a.getStatus() != Account.Status.ACTIVE) {
            throw new BusinessRuleException("Account " + a.getAccountNumber() + " is " + a.getStatus() + " and cannot be used.");
        }
    }

    private void requirePositive(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) throw new BusinessRuleException("Amount must be greater than zero.");
    }

    // ---------------- helpers ----------------

    private Account lock(String accountNumber) {
        return accounts.findForUpdate(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account " + accountNumber + " does not exist."));
    }

    private BankTransaction credit(Account a, BigDecimal amount, Type type, String desc, String ref) {
        a.setBalance(a.getBalance().add(amount));
        accounts.save(a);
        return record(a, type, amount, desc, ref);
    }

    private BankTransaction debit(Account a, BigDecimal amount, Type type, String desc, String ref) {
        a.setBalance(a.getBalance().subtract(amount));
        accounts.save(a);
        return record(a, type, amount, desc, ref);
    }

    BankTransaction record(Account a, Type type, BigDecimal amount, String desc, String ref) {
        BankTransaction t = new BankTransaction();
        t.setAccount(a);
        t.setType(type);
        t.setAmount(amount);
        t.setBalanceAfter(a.getBalance());
        t.setTxnTime(LocalDateTime.now());
        t.setDescription(desc);
        t.setReferenceNo(ref);
        return txns.save(t);
    }

    private String orDefault(String s, String d) { return (s == null || s.isBlank()) ? d : s.trim(); }
}
