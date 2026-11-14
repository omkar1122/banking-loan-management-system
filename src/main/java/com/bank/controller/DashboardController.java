package com.bank.controller;

import com.bank.model.EmiInstallment;
import com.bank.model.Loan;
import com.bank.model.LoanApplication;
import com.bank.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
public class DashboardController {
    private final CustomerRepository customers;
    private final BranchRepository branches;
    private final AccountRepository accounts;
    private final BankTransactionRepository txns;
    private final LoanApplicationRepository applications;
    private final LoanRepository loans;
    private final EmiInstallmentRepository emis;

    public DashboardController(CustomerRepository customers, BranchRepository branches, AccountRepository accounts,
                               BankTransactionRepository txns, LoanApplicationRepository applications,
                               LoanRepository loans, EmiInstallmentRepository emis) {
        this.customers = customers; this.branches = branches; this.accounts = accounts;
        this.txns = txns; this.applications = applications; this.loans = loans; this.emis = emis;
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        BigDecimal total = accounts.totalDeposits();
        model.addAttribute("customerCount", customers.count());
        model.addAttribute("branchCount", branches.count());
        model.addAttribute("accountCount", accounts.count());
        model.addAttribute("totalDeposits", total == null ? BigDecimal.ZERO : total);
        model.addAttribute("pendingApplications", applications.countByStatus(LoanApplication.Status.PENDING));
        model.addAttribute("activeLoans", loans.countByStatus(Loan.Status.ACTIVE));
        model.addAttribute("overdueEmis", emis.countByStatusAndDueDateBefore(EmiInstallment.Status.PENDING, LocalDate.now()));
        model.addAttribute("todayTxnCount", txns.countByTxnTimeGreaterThanEqual(LocalDate.now().atStartOfDay()));
        model.addAttribute("recent", txns.findTop10ByOrderByTxnTimeDesc());
        return "dashboard";
    }
}
