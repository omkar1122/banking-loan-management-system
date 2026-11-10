package com.bank.repository;

import com.bank.model.EmiInstallment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EmiInstallmentRepository extends JpaRepository<EmiInstallment, Long> {
    List<EmiInstallment> findByLoanIdOrderByInstallmentNo(Long loanId);

    Optional<EmiInstallment> findFirstByLoanIdAndStatusOrderByInstallmentNo(Long loanId, EmiInstallment.Status status);

    /** Custom query: unpaid instalments whose due date has passed. */
    @Query("select e from EmiInstallment e where e.status = :status and e.dueDate < :today order by e.dueDate")
    List<EmiInstallment> findOverdue(@Param("status") EmiInstallment.Status status, @Param("today") LocalDate today);

    long countByStatusAndDueDateBefore(EmiInstallment.Status status, LocalDate date);
}
