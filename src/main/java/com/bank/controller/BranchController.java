package com.bank.controller;

import com.bank.exception.ResourceNotFoundException;
import com.bank.model.Branch;
import com.bank.repository.BranchRepository;
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
@RequestMapping("/branches")
public class BranchController {
    private static final Set<String> SORTS = Set.of("name", "city", "ifscCode");
    private final BranchRepository repo;

    public BranchController(BranchRepository repo) { this.repo = repo; }

    @GetMapping
    public String list(@RequestParam(required = false) String q,
                       @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
                       @RequestParam(defaultValue = "name") String sort, @RequestParam(defaultValue = "asc") String dir,
                       Model model) {
        Page<Branch> data = repo.search(PageUtil.blankToNull(q), PageUtil.of(page, size, sort, dir, SORTS, "name"));
        model.addAttribute("data", data);
        model.addAttribute("sort", sort); model.addAttribute("dir", dir); model.addAttribute("size", size);
        model.addAttribute("pagerPath", "/branches");
        return "branches/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("branch", new Branch());
        return "branches/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("branch", repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch #" + id + " was not found.")));
        return "branches/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("branch") Branch branch, BindingResult result, RedirectAttributes ra) {
        if (branch.getId() == null && branch.getIfscCode() != null && repo.existsByIfscCode(branch.getIfscCode())) {
            result.rejectValue("ifscCode", "dup", "This IFSC code already exists");
        }
        if (result.hasErrors()) return "branches/form";
        boolean isNew = branch.getId() == null;
        repo.save(branch);
        ra.addFlashAttribute("flashSuccess", "Branch " + (isNew ? "created" : "updated") + " successfully.");
        return "redirect:/branches";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        Branch b = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Branch #" + id + " was not found."));
        try {
            repo.delete(b);
            repo.flush();
            ra.addFlashAttribute("flashSuccess", "Branch deleted.");
        } catch (DataIntegrityViolationException ex) {
            ra.addFlashAttribute("flashError", "Cannot delete a branch that still has accounts or employees.");
        }
        return "redirect:/branches";
    }
}
