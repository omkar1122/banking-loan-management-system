package com.bank.repository;

import com.bank.model.LoanType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanTypeRepository extends JpaRepository<LoanType, Long> {
    boolean existsByName(String name);
}
