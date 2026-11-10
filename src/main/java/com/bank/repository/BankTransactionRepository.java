package com.bank.repository;

import com.bank.model.BankTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface BankTransactionRepository extends JpaRepository<BankTransaction, Long> {

    /** Custom query: account statement for a date range (paged and sortable). */
    Page<BankTransaction> findByAccountIdAndTxnTimeBetween(Long accountId, LocalDateTime from, LocalDateTime to, Pageable pageable);

    /** Custom query: sum of debits of the given types in a time window (daily withdrawal limit rule). */
    @Query("select sum(t.amount) from BankTransaction t where t.account.id = :accountId and t.type in :types "
         + "and t.txnTime >= :from and t.txnTime < :to")
    BigDecimal sumAmount(@Param("accountId") Long accountId, @Param("types") Collection<BankTransaction.Type> types,
                         @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    /** Custom query: latest activity for the dashboard. */
    List<BankTransaction> findTop10ByOrderByTxnTimeDesc();

    long countByTxnTimeGreaterThanEqual(LocalDateTime since);
}
