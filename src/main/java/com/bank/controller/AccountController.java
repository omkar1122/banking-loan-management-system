package com.bank.controller;

import com.bank.dto.AccountForm;
import com.bank.exception.BusinessRuleException;
import com.bank.exception.ResourceNotFoundException;
import com.bank.model.Account;
import com.bank.model.BankTransaction;
import com.bank.model.Customer;
import com.bank.repository.*;
import com.bank.service.AccountService;
import com.bank.util.PageUtil;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;
import java.util.Set;

@Controller
@RequestMapping("/accounts")
public class AccountController {
    private static final Set<String> SORTS = Set.of("accountNumber", "balance", "openedOn");
    private static final Set<String> TXN_SORTS = Set.of("txnTime", "amount", "type");

    private final AccountRepository accounts;
    private final BranchRepository branches;
    private final AccountTypeRepository types;
    private final CustomerRepository customers;
    private final BankTransactionRepository txns;
    private final AccountService service;

    public AccountController(AccountRepository accounts, BranchRepository branches, AccountTypeRepository types,
                             CustomerRepository customers, BankTransactionRepository txns, AccountService service) {
        this.accounts = accounts; this.branches = branches; this.types = types;
        this.customers = customers; this.txns = txns; this.service = service;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String q, @RequestParam(required = false) Long branchId,
                       @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size,
                       @RequestParam(defaultValue = "accountNumber") String sort, @RequestParam(defaultValue = "asc") String dir,
                       Model model) {
        Page<Account> data = accounts.search(branchId, PageUtil.blankToNull(q),
                PageUtil.of(page, size, sort, dir, SORTS, "accountNumber"));
        model.addAttribute("data", data);
        model.addAttribute("branches", branches.findAll());
        model.addAttribute("sort", sort); model.addAttribute("dir", dir); model.addAttribute("size", size);
        model.addAttribute("pagerPath", "/accounts");
        return "accounts/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("accountForm", new AccountForm());
        addLookups(model, false);
        return "accounts/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Account a = service.get(id);
        AccountForm f = new AccountForm();
        f.setId(a.getId());
        f.setBranchId(a.getBranch().getId());
        f.setAccountTypeId(a.getAccountType().getId());
        f.setStatus(a.getStatus().name());
        a.getHolders().forEach(h -> f.getHolderIds().add(h.getId()));
        model.addAttribute("accountForm", f);
        model.addAttribute("accountNumber", a.getAccountNumber());
        addLookups(model, true);
        return "accounts/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("accountForm") AccountForm form, BindingResult result,
                       Model model, RedirectAttributes ra) {
        boolean editing = form.getId() != null;
        if (!result.hasErrors()) {
            try {
                if (editing) service.update(form); else service.create(form);
                ra.addFlashAttribute("flashSuccess", "Account " + (editing ? "updated" : "opened") + " successfully.");
                return "redirect:/accounts";
            } catch (BusinessRuleException ex) {
                result.reject("rule", ex.getMessage());
            }
        }
        if (editing) model.addAttribute("accountNumber", service.get(form.getId()).getAccountNumber());
        addLookups(model, editing);
        return "accounts/form";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            service.delete(id);
            ra.addFlashAttribute("flashSuccess", "Account deleted.");
        } catch (BusinessRuleException ex) {
            ra.addFlashAttribute("flashError", ex.getMessage());
        } catch (DataIntegrityViolationException ex) {
            ra.addFlashAttribute("flashError", "This account has transaction history and cannot be deleted. Set its status to CLOSED instead.");
        }
        return "redirect:/accounts";
    }

    /** Account statement: date-range filter, pagination and sorting. */
    @GetMapping("/{id}/statement")
    public String statement(@PathVariable Long id,
                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
                            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
                            @RequestParam(defaultValue = "txnTime") String sort, @RequestParam(defaultValue = "desc") String dir,
                            Model model) {
        Account a = service.get(id);
        LocalDate f = from == null ? LocalDate.of(2000, 1, 1) : from;
        LocalDate t = to == null ? LocalDate.now() : to;
        Page<BankTransaction> data = txns.findByAccountIdAndTxnTimeBetween(id, f.atStartOfDay(), t.plusDays(1).atStartOfDay(),
                PageUtil.of(page, size, sort, dir, TXN_SORTS, "txnTime"));
        model.addAttribute("account", a);
        model.addAttribute("data", data);
        model.addAttribute("sort", sort); model.addAttribute("dir", dir); model.addAttribute("size", size);
        model.addAttribute("pagerPath", "/accounts/" + id + "/statement");
        return "accounts/statement";
    }

    /** Page that hosts the AJAX balance lookup. */
    @GetMapping("/balance")
    public String balancePage() { return "accounts/balance"; }

    private void addLookups(Model model, boolean editing) {
        model.addAttribute("branches", branches.findAll());
        model.addAttribute("accountTypes", types.findAll());
        model.addAttribute("customers", customers.findAll());
        model.addAttribute("editing", editing);
    }
}
