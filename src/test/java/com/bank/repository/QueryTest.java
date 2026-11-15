package com.bank.repository;

import com.bank.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

/** Custom query tests against an in-memory H2 database. */
@DataJpaTest
class QueryTest {
    @Autowired TestEntityManager em;
    @Autowired AccountRepository accounts;

    @BeforeEach
    void data() {
        Branch b1 = branch("North", "NOVA0000201");
        Branch b2 = branch("South", "NOVA0000202");
        AccountType type = new AccountType();
        type.setName("SAVINGS"); type.setMinBalance(new BigDecimal("1000")); type.setDailyWithdrawLimit(new BigDecimal("50000"));
        em.persist(type);
        Customer c1 = customer("Asha", "AAAAA1111A");
        Customer c2 = customer("Bilal", "BBBBB2222B");
        account("A1", b1, type, "800.00", Set.of(c1));
        account("A2", b1, type, "9000.00", Set.of(c1));
        account("A3", b2, type, "3000.00", Set.of(c2));
        em.flush();
    }

    @Test
    void lowBalanceReturnsOnlyAccountsBelowThreshold() {
        List<Account> low = accounts.findLowBalance(new BigDecimal("5000"), Account.Status.ACTIVE);
        assertEquals(2, low.size());
        assertEquals("A1", low.get(0).getAccountNumber());   // ordered by balance ascending
        assertEquals("A3", low.get(1).getAccountNumber());
    }

    @Test
    void branchTotalsGroupsCountsAndSums() {
        List<Object[]> rows = accounts.branchTotals();
        assertEquals(2, rows.size());
        Object[] north = rows.get(0);                        // highest total first
        assertEquals("North", north[0]);
        assertEquals(2L, ((Number) north[1]).longValue());
        assertEquals(0, new BigDecimal("9800.00").compareTo((BigDecimal) north[2]));
    }

    @Test
    void topDepositorsSumsAcrossAllAccountsOfACustomer() {
        List<Object[]> top = accounts.topDepositors(PageRequest.of(0, 1));
        assertEquals(1, top.size());
        assertEquals("Asha", top.get(0)[0]);
        assertEquals(0, new BigDecimal("9800.00").compareTo((BigDecimal) top.get(0)[2]));
    }

    // ---- helpers ----
    private Branch branch(String name, String ifsc) {
        Branch b = new Branch(); b.setName(name); b.setIfscCode(ifsc); b.setCity(name); return em.persist(b);
    }
    private Customer customer(String name, String pan) {
        Customer c = new Customer();
        c.setFullName(name); c.setEmail(name + "@x.com"); c.setPhone("9999999999"); c.setPan(pan);
        c.setDob(java.time.LocalDate.of(1990, 1, 1)); c.setMonthlyIncome(new BigDecimal("50000"));
        return em.persist(c);
    }
    private void account(String no, Branch b, AccountType t, String bal, Set<Customer> holders) {
        Account a = new Account();
        a.setAccountNumber(no); a.setBranch(b); a.setAccountType(t); a.setBalance(new BigDecimal(bal));
        a.setHolders(new java.util.HashSet<>(holders));
        em.persist(a);
    }
}
