package com.bank.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "branch")
@Getter @Setter
public class Branch {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Branch name is required")
    @Size(max = 100)
    private String name;

    @NotBlank(message = "IFSC code is required")
    @Pattern(regexp = "[A-Z]{4}0[A-Z0-9]{6}", message = "IFSC must look like ABCD0123456")
    @Column(unique = true, length = 11)
    private String ifscCode;

    @NotBlank(message = "City is required")
    private String city;

    private String address;

    @Pattern(regexp = "|\\d{10}", message = "Phone must be 10 digits")
    private String phone;
}
