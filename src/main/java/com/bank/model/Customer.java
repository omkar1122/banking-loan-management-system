package com.bank.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "customer")
@Getter @Setter
public class Customer {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Full name is required")
    @Size(max = 100)
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email")
    private String email;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "\\d{10}", message = "Phone must be 10 digits")
    private String phone;

    @NotBlank(message = "PAN is required")
    @Pattern(regexp = "[A-Z]{5}[0-9]{4}[A-Z]", message = "PAN must look like ABCDE1234F")
    @Column(unique = true, length = 10)
    private String pan;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dob;

    private String address;

    @NotNull(message = "Monthly income is required")
    @DecimalMin(value = "0.00", message = "Income cannot be negative")
    @Column(precision = 15, scale = 2)
    private BigDecimal monthlyIncome;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    /** Inverse side of the many-to-many (joint holders) relationship. */
    @ManyToMany(mappedBy = "holders")
    private Set<Account> accounts = new HashSet<>();

    @PrePersist
    void onCreate() { createdAt = LocalDateTime.now(); }
}
