package com.bank.repository;

import com.bank.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByStatusOrderByIdDesc(Loan.Status status);
    long countByStatus(Loan.Status status);
}
