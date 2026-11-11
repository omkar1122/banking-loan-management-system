package com.bank.controller;

import com.bank.exception.ResourceNotFoundException;
import com.bank.model.Customer;
import com.bank.repository.CustomerRepository;
import com.bank.util.PageUtil;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Set;

@Controller
@RequestMapping("/customers")
public class CustomerController {
    private static final Set<String> SORTS = Set.of("fullName", "pan", "monthlyIncome", "createdAt");
    private final CustomerRepository repo;

    public CustomerController(CustomerRepository repo) { this.repo = repo; }

    @GetMapping
    public String list(@RequestParam(required = false) String q,
                       @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size,
                       @RequestParam(defaultValue = "fullName") String sort, @RequestParam(defaultValue = "asc") String dir,
                       Model model) {
        Page<Customer> data = repo.search(PageUtil.blankToNull(q), PageUtil.of(page, size, sort, dir, SORTS, "fullName"));
        model.addAttribute("data", data);
        model.addAttribute("sort", sort); model.addAttribute("dir", dir); model.addAttribute("size", size);
        model.addAttribute("pagerPath", "/customers");
        return "customers/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("customer", new Customer());
        return "customers/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("customer", repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer #" + id + " was not found.")));
        return "customers/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("customer") Customer customer, BindingResult result, RedirectAttributes ra) {
        repo.findByPan(customer.getPan()).ifPresent(existing -> {
            if (!existing.getId().equals(customer.getId())) result.rejectValue("pan", "dup", "This PAN is already registered");
        });
        if (result.hasErrors()) return "customers/form";
        boolean isNew = customer.getId() == null;
        repo.save(customer);
        ra.addFlashAttribute("flashSuccess", "Customer " + (isNew ? "created" : "updated") + " successfully.");
        return "redirect:/customers";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        Customer c = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Customer #" + id + " was not found."));
        try {
            repo.delete(c);
            repo.flush();
            ra.addFlashAttribute("flashSuccess", "Customer deleted.");
        } catch (DataIntegrityViolationException ex) {
            ra.addFlashAttribute("flashError", "Cannot delete a customer who still has accounts or loans.");
        }
        return "redirect:/customers";
    }
}
