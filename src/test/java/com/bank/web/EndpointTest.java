package com.bank.web;

import com.bank.model.*;
import com.bank.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Endpoint tests: real controllers, services and repositories on H2; each test rolls back. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class EndpointTest {
    @Autowired MockMvc mvc;
    @Autowired BranchRepository branches;
    @Autowired AccountTypeRepository types;
    @Autowired CustomerRepository customers;
    @Autowired AccountRepository accounts;

    @BeforeEach
    void seed() {
        Branch b = new Branch(); b.setName("Test Branch"); b.setIfscCode("NOVA0000999"); b.setCity("Bhopal");
        branches.save(b);
        AccountType t = new AccountType();
        t.setName("SAVINGS"); t.setMinBalance(new BigDecimal("1000")); t.setDailyWithdrawLimit(new BigDecimal("50000"));
        types.save(t);
        Customer c = new Customer();
        c.setFullName("Test User"); c.setEmail("t@x.com"); c.setPhone("9999999999"); c.setPan("TESTU1234T");
        c.setDob(LocalDate.of(1990, 1, 1)); c.setMonthlyIncome(new BigDecimal("40000"));
        customers.save(c);
        Account a = new Account();
        a.setAccountNumber("ACTEST0001"); a.setBranch(b); a.setAccountType(t); a.setBalance(new BigDecimal("5000.00"));
        a.setHolders(new HashSet<>(Set.of(c)));
        accounts.save(a);
    }

    @Test
    void dashboardLoads() throws Exception {
        mvc.perform(get("/")).andExpect(status().isOk()).andExpect(view().name("dashboard"))
           .andExpect(model().attributeExists("customerCount", "recent"));
    }

    @Test
    void balanceApiReturnsJsonForKnownAccountAnd404ForUnknown() throws Exception {
        mvc.perform(get("/api/accounts/ACTEST0001/balance"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.accountNumber").value("ACTEST0001"))
           .andExpect(jsonPath("$.balance").value(5000.00))
           .andExpect(jsonPath("$.available").value(4000.00));
        mvc.perform(get("/api/accounts/NOPE/balance")).andExpect(status().isNotFound());
    }

    @Test
    void depositPostRedirectsAndUpdatesBalance() throws Exception {
        mvc.perform(post("/transactions/deposit").param("accountNumber", "ACTEST0001").param("amount", "250.50"))
           .andExpect(status().is3xxRedirection())
           .andExpect(flash().attributeExists("flashSuccess"));
        assertEquals(0, new BigDecimal("5250.50").compareTo(accounts.findByAccountNumber("ACTEST0001").orElseThrow().getBalance()));
    }

    @Test
    void withdrawalBreakingMinimumBalanceShowsErrorAndKeepsBalance() throws Exception {
        mvc.perform(post("/transactions/withdraw").param("accountNumber", "ACTEST0001").param("amount", "4500"))
           .andExpect(status().isOk())
           .andExpect(view().name("transactions/cash"))
           .andExpect(model().attributeHasErrors("cashForm"));
        assertEquals(0, new BigDecimal("5000.00").compareTo(accounts.findByAccountNumber("ACTEST0001").orElseThrow().getBalance()));
    }

    @Test
    void invalidCustomerFormIsRedisplayedWithFieldErrors() throws Exception {
        mvc.perform(post("/customers/save").param("fullName", "").param("email", "bad").param("phone", "123")
                .param("pan", "xx").param("monthlyIncome", "-5"))
           .andExpect(status().isOk())
           .andExpect(view().name("customers/form"))
           .andExpect(model().attributeHasFieldErrors("customer", "fullName", "email", "phone", "pan", "dob", "monthlyIncome"));
    }

    @Test
    void missingRecordShowsFriendlyErrorPageWith404() throws Exception {
        mvc.perform(get("/customers/999999/edit"))
           .andExpect(status().isNotFound())
           .andExpect(view().name("error"))
           .andExpect(model().attributeExists("errMessage"));
    }
}
