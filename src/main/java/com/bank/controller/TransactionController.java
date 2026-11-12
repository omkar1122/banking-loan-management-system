package com.bank.controller;

import com.bank.dto.CashForm;
import com.bank.dto.TransferForm;
import com.bank.exception.BusinessRuleException;
import com.bank.exception.ResourceNotFoundException;
import com.bank.model.BankTransaction;
import com.bank.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/transactions")
public class TransactionController {
    private final TransactionService service;

    public TransactionController(TransactionService service) { this.service = service; }

    @GetMapping("/{mode:deposit|withdraw}")
    public String cashForm(@PathVariable String mode, Model model) {
        model.addAttribute("cashForm", new CashForm());
        model.addAttribute("mode", mode);
        return "transactions/cash";
    }

    @PostMapping("/{mode:deposit|withdraw}")
    public String cashSubmit(@PathVariable String mode, @Valid @ModelAttribute("cashForm") CashForm form,
                             BindingResult result, Model model, RedirectAttributes ra) {
        model.addAttribute("mode", mode);
        if (result.hasErrors()) return "transactions/cash";
        try {
            BankTransaction t = "deposit".equals(mode)
                    ? service.deposit(form.getAccountNumber().trim(), form.getAmount(), form.getDescription())
                    : service.withdraw(form.getAccountNumber().trim(), form.getAmount(), form.getDescription());
            ra.addFlashAttribute("flashSuccess", (mode.equals("deposit") ? "Deposited " : "Withdrew ") + t.getAmount()
                    + ". New balance: " + t.getBalanceAfter());
            return "redirect:/transactions/" + mode;
        } catch (BusinessRuleException ex) {
            result.reject("rule", ex.getMessage());
        } catch (ResourceNotFoundException ex) {
            result.rejectValue("accountNumber", "missing", ex.getMessage());
        }
        return "transactions/cash";
    }

    @GetMapping("/transfer")
    public String transferForm(Model model) {
        model.addAttribute("transferForm", new TransferForm());
        return "transactions/transfer";
    }

    @PostMapping("/transfer")
    public String transferSubmit(@Valid @ModelAttribute("transferForm") TransferForm form, BindingResult result,
                                 RedirectAttributes ra) {
        if (result.hasErrors()) return "transactions/transfer";
        try {
            String ref = service.transfer(form.getFromAccount().trim(), form.getToAccount().trim(), form.getAmount(), form.getRemarks());
            ra.addFlashAttribute("flashSuccess", "Transfer successful. Reference " + ref + ".");
            return "redirect:/transactions/transfer";
        } catch (BusinessRuleException ex) {
            result.reject("rule", ex.getMessage());
        } catch (ResourceNotFoundException ex) {
            result.reject("missing", ex.getMessage());
        }
        return "transactions/transfer";
    }
}
