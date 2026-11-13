package com.bank.controller;

import com.bank.dto.LoanApplicationForm;
import com.bank.exception.BusinessRuleException;
import com.bank.exception.ResourceNotFoundException;
import com.bank.model.*;
import com.bank.repository.*;
import com.bank.service.LoanService;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/loans")
public class LoanController {
    private final LoanTypeRepository loanTypes;
    private final LoanApplicationRepository applications;
    private final LoanRepository loans;
    private final EmiInstallmentRepository emis;
    private final CustomerRepository customers;
    private final AccountRepository accounts;
    private final LoanService service;

    public LoanController(LoanTypeRepository loanTypes, LoanApplicationRepository applications, LoanRepository loans,
                          EmiInstallmentRepository emis, CustomerRepository customers, AccountRepository accounts,
                          LoanService service) {
        this.loanTypes = loanTypes; this.applications = applications; this.loans = loans;
        this.emis = emis; this.customers = customers; this.accounts = accounts; this.service = service;
    }

    // ---------- loan types: full CRUD ----------
    @GetMapping("/types")
    public String types(Model model) {
        model.addAttribute("types", loanTypes.findAll());
        return "loans/types";
    }

    @GetMapping("/types/new")
    public String typeForm(Model model) {
        model.addAttribute("loanType", new LoanType());
        return "loans/type-form";
    }

    @GetMapping("/types/{id}/edit")
    public String typeEdit(@PathVariable Long id, Model model) {
        model.addAttribute("loanType", loanTypes.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loan type #" + id + " was not found.")));
        return "loans/type-form";
    }

    @PostMapping("/types/save")
    public String typeSave(@Valid @ModelAttribute("loanType") LoanType lt, BindingResult result, RedirectAttributes ra) {
        if (lt.getId() == null && lt.getName() != null && loanTypes.existsByName(lt.getName())) {
            result.rejectValue("name", "dup", "A loan type with this name already exists");
        }
        if (result.hasErrors()) return "loans/type-form";
        boolean isNew = lt.getId() == null;
        loanTypes.save(lt);
        ra.addFlashAttribute("flashSuccess", "Loan type " + (isNew ? "created" : "updated") + ".");
        return "redirect:/loans/types";
    }

    @PostMapping("/types/{id}/delete")
    public String typeDelete(@PathVariable Long id, RedirectAttributes ra) {
        LoanType lt = loanTypes.findById(id).orElseThrow(() -> new ResourceNotFoundException("Loan type #" + id + " was not found."));
        try {
            loanTypes.delete(lt);
            loanTypes.flush();
            ra.addFlashAttribute("flashSuccess", "Loan type deleted.");
        } catch (DataIntegrityViolationException ex) {
            ra.addFlashAttribute("flashError", "This loan type is used by existing applications or loans.");
        }
        return "redirect:/loans/types";
    }

    // ---------- apply ----------
    @GetMapping("/apply")
    public String applyForm(Model model) {
        model.addAttribute("loanApplicationForm", new LoanApplicationForm());
        addApplyLookups(model);
        return "loans/apply";
    }

    @PostMapping("/apply")
    public String applySubmit(@Valid @ModelAttribute("loanApplicationForm") LoanApplicationForm form,
                              BindingResult result, Model model, RedirectAttributes ra) {
        if (!result.hasErrors()) {
            try {
                LoanApplication app = service.apply(form);
                ra.addFlashAttribute("flashSuccess", "Application #" + app.getId() + " submitted and is awaiting approval.");
                return "redirect:/loans/applications";
            } catch (BusinessRuleException | ResourceNotFoundException ex) {
                result.reject("rule", ex.getMessage());
            }
        }
        addApplyLookups(model);
        return "loans/apply";
    }

    // ---------- approval workflow ----------
    @GetMapping("/applications")
    public String applicationList(@RequestParam(defaultValue = "PENDING") String status, Model model) {
        LoanApplication.Status st;
        try { st = LoanApplication.Status.valueOf(status.toUpperCase()); } catch (IllegalArgumentException e) { st = LoanApplication.Status.PENDING; }
        model.addAttribute("applications", applications.findByStatusOrderByAppliedOnDescIdDesc(st));
        model.addAttribute("status", st.name());
        return "loans/applications";
    }

    @PostMapping("/applications/{id}/approve")
    public String approve(@PathVariable Long id, @RequestParam(required = false) String remarks, RedirectAttributes ra) {
        try {
            Loan loan = service.approve(id, remarks);
            ra.addFlashAttribute("flashSuccess", "Application approved. Loan #" + loan.getId() + " created, EMI "
                    + loan.getEmiAmount() + " x " + loan.getTenureMonths() + " months; amount credited to the account.");
        } catch (BusinessRuleException ex) {
            ra.addFlashAttribute("flashError", ex.getMessage());
        }
        return "redirect:/loans/applications";
    }

    @PostMapping("/applications/{id}/reject")
    public String reject(@PathVariable Long id, @RequestParam(required = false) String remarks, RedirectAttributes ra) {
        try {
            service.reject(id, remarks);
            ra.addFlashAttribute("flashSuccess", "Application rejected.");
        } catch (BusinessRuleException ex) {
            ra.addFlashAttribute("flashError", ex.getMessage());
        }
        return "redirect:/loans/applications";
    }

    // ---------- loans and EMI ----------
    @GetMapping
    public String loanList(Model model) {
        model.addAttribute("activeLoans", loans.findByStatusOrderByIdDesc(Loan.Status.ACTIVE));
        model.addAttribute("closedLoans", loans.findByStatusOrderByIdDesc(Loan.Status.CLOSED));
        return "loans/list";
    }

    @GetMapping("/{id}/emi")
    public String emiSchedule(@PathVariable Long id, Model model) {
        Loan loan = loans.findById(id).orElseThrow(() -> new ResourceNotFoundException("Loan #" + id + " was not found."));
        model.addAttribute("loan", loan);
        model.addAttribute("schedule", emis.findByLoanIdOrderByInstallmentNo(id));
        return "loans/emi";
    }

    @PostMapping("/{id}/pay-emi")
    public String payEmi(@PathVariable Long id, RedirectAttributes ra) {
        try {
            EmiInstallment e = service.payNextEmi(id);
            ra.addFlashAttribute("flashSuccess", "EMI " + e.getInstallmentNo() + " paid (" + e.getEmiAmount() + ").");
        } catch (BusinessRuleException ex) {
            ra.addFlashAttribute("flashError", ex.getMessage());
        }
        return "redirect:/loans/" + id + "/emi";
    }

    private void addApplyLookups(Model model) {
        model.addAttribute("customers", customers.findAll());
        model.addAttribute("loanTypes", loanTypes.findAll());
        model.addAttribute("accounts", accounts.findAll());
    }
}
