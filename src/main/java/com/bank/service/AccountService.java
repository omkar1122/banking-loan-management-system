package com.bank.service;

import com.bank.dto.AccountForm;
import com.bank.exception.BusinessRuleException;
import com.bank.exception.ResourceNotFoundException;
import com.bank.model.*;
import com.bank.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class AccountService {
    private final AccountRepository accounts;
    private final BranchRepository branches;
    private final AccountTypeRepository types;
    private final CustomerRepository customers;
    private final BankTransactionRepository txns;

    public AccountService(AccountRepository accounts, BranchRepository branches, AccountTypeRepository types,
                          CustomerRepository customers, BankTransactionRepository txns) {
        this.accounts = accounts; this.branches = branches; this.types = types;
        this.customers = customers; this.txns = txns;
    }

    public Account get(Long id) {
        return accounts.findById(id).orElseThrow(() -> new ResourceNotFoundException("Account #" + id + " was not found."));
    }

    /** Opening an account also writes the opening-deposit ledger row; both succeed or neither does. */
    @Transactional
    public Account create(AccountForm f) {
        Branch branch = branches.findById(f.getBranchId()).orElseThrow(() -> new ResourceNotFoundException("Branch not found."));
        AccountType type = types.findById(f.getAccountTypeId()).orElseThrow(() -> new ResourceNotFoundException("Account type not found."));
        BigDecimal deposit = f.getInitialDeposit() == null ? BigDecimal.ZERO : f.getInitialDeposit();
        if (deposit.compareTo(type.getMinBalance()) < 0) {
            throw new BusinessRuleException("Opening deposit must be at least the minimum balance of " + type.getMinBalance() + " for " + type.getName() + " accounts.");
        }
        Account a = new Account();
        a.setAccountNumber(newAccountNumber());
        a.setBranch(branch);
        a.setAccountType(type);
        a.setBalance(deposit);
        a.setOpenedOn(LocalDate.now());
        a.setHolders(new HashSet<>(customers.findAllById(f.getHolderIds())));
        if (a.getHolders().isEmpty()) throw new BusinessRuleException("At least one valid account holder is required.");
        a = accounts.save(a);
        if (deposit.signum() > 0) {
            BankTransaction t = new BankTransaction();
            t.setAccount(a); t.setType(BankTransaction.Type.DEPOSIT); t.setAmount(deposit);
            t.setBalanceAfter(deposit); t.setTxnTime(LocalDateTime.now()); t.setDescription("Opening deposit");
            txns.save(t);
        }
        return a;
    }

    /** Update: branch, holders and status only. Balance can change only through transactions. */
    @Transactional
    public Account update(AccountForm f) {
        Account a = get(f.getId());
        a.setBranch(branches.findById(f.getBranchId()).orElseThrow(() -> new ResourceNotFoundException("Branch not found.")));
        a.setHolders(new HashSet<>(customers.findAllById(f.getHolderIds())));
        Account.Status newStatus = Account.Status.valueOf(f.getStatus());
        if (newStatus == Account.Status.CLOSED && a.getBalance().signum() != 0) {
            throw new BusinessRuleException("An account can be closed only when its balance is zero.");
        }
        a.setStatus(newStatus);
        return accounts.save(a);
    }

    @Transactional
    public void delete(Long id) {
        Account a = get(id);
        if (a.getBalance().signum() != 0) throw new BusinessRuleException("Cannot delete an account with a non-zero balance.");
        accounts.delete(a);
        accounts.flush();   // surfaces FK violations (existing ledger rows) here
    }

    private String newAccountNumber() {
        String no;
        do { no = String.format("AC%010d", ThreadLocalRandom.current().nextLong(1_000_000_000L, 9_999_999_999L)); }
        while (accounts.findByAccountNumber(no).isPresent());
        return no;
    }
}
