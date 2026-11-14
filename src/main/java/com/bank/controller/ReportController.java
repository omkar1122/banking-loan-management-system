package com.bank.controller;

import com.bank.model.Account;
import com.bank.model.EmiInstallment;
import com.bank.repository.AccountRepository;
import com.bank.repository.EmiInstallmentRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
@RequestMapping("/reports")
public class ReportController {
    private final AccountRepository accounts;
    private final EmiInstallmentRepository emis;

    public ReportController(AccountRepository accounts, EmiInstallmentRepository emis) {
        this.accounts = accounts; this.emis = emis;
    }

    @GetMapping("/branch-totals")
    public String branchTotals(Model model) {
        model.addAttribute("rows", accounts.branchTotals());
        return "reports/branch-totals";
    }

    @GetMapping("/top-depositors")
    public String topDepositors(@RequestParam(defaultValue = "5") int n, Model model) {
        int limit = Math.min(Math.max(n, 1), 50);
        model.addAttribute("rows", accounts.topDepositors(PageRequest.of(0, limit)));
        model.addAttribute("n", limit);
        return "reports/top-depositors";
    }

    @GetMapping("/overdue-emis")
    public String overdue(Model model) {
        model.addAttribute("rows", emis.findOverdue(EmiInstallment.Status.PENDING, LocalDate.now()));
        model.addAttribute("today", LocalDate.now());
        return "reports/overdue-emis";
    }

    @GetMapping("/low-balance")
    public String lowBalance(@RequestParam(defaultValue = "5000") BigDecimal threshold, Model model) {
        model.addAttribute("rows", accounts.findLowBalance(threshold, Account.Status.ACTIVE));
        model.addAttribute("threshold", threshold);
        return "reports/low-balance";
    }
}
