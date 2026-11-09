package com.bank.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "account")
@Getter @Setter
public class Account {
    public enum Status { ACTIVE, FROZEN, CLOSED }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 20)
    private String accountNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "branch_id")
    private Branch branch;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_type_id")
    private AccountType accountType;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Status status = Status.ACTIVE;

    private LocalDate openedOn;

    /** Owning side of the many-to-many relationship Customer N--M Account (joint holders). */
    @ManyToMany
    @JoinTable(name = "account_holder",
            joinColumns = @JoinColumn(name = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "customer_id"))
    private Set<Customer> holders = new HashSet<>();

    public String getHolderNames() {
        StringBuilder sb = new StringBuilder();
        for (Customer c : holders) { if (sb.length() > 0) sb.append(", "); sb.append(c.getFullName()); }
        return sb.toString();
    }
}
