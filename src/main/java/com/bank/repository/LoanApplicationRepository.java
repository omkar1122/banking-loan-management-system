package com.bank.repository;

import com.bank.model.LoanApplication;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface LoanApplicationRepository extends JpaRepository<LoanApplication, Long> {
    /** Custom query: applications by status, newest first (pending queue). */
    List<LoanApplication> findByStatusOrderByAppliedOnDescIdDesc(LoanApplication.Status status);

    long countByStatus(LoanApplication.Status status);

    /** Locks the row so two officers cannot approve the same application twice. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from LoanApplication a where a.id = :id")
    Optional<LoanApplication> findForUpdate(@Param("id") Long id);
}
