package com.bank.controller;

import com.bank.model.Account;
import com.bank.repository.AccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** JSON endpoints used by the AJAX features. */
@RestController
@RequestMapping("/api")
public class ApiController {
    private final AccountRepository accounts;

    public ApiController(AccountRepository accounts) { this.accounts = accounts; }

    /** AJAX feature 1: balance lookup by account number. */
    @GetMapping("/accounts/{accountNumber}/balance")
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> balance(@PathVariable String accountNumber) {
        return accounts.findByAccountNumber(accountNumber.trim()).map(a -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("accountNumber", a.getAccountNumber());
            m.put("balance", a.getBalance());
            m.put("status", a.getStatus().name());
            m.put("minBalance", a.getAccountType().getMinBalance());
            m.put("available", a.getBalance().subtract(a.getAccountType().getMinBalance()).max(java.math.BigDecimal.ZERO));
            return ResponseEntity.ok(m);
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Account not found")));
    }

    /** AJAX feature 2: destination account check on the transfer form (returns holder names). */
    @GetMapping("/accounts/{accountNumber}/holders")
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> holders(@PathVariable String accountNumber) {
        return accounts.findByAccountNumber(accountNumber.trim()).map(a -> {
            Map<String, Object> m = new LinkedHashMap<>();
            List<String> names = a.getHolders().stream().map(h -> h.getFullName()).sorted().toList();
            m.put("accountNumber", a.getAccountNumber());
            m.put("holders", names);
            m.put("status", a.getStatus().name());
            m.put("branch", a.getBranch().getName());
            return ResponseEntity.ok(m);
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Account not found")));
    }
}
