package com.bank.repository;

import com.bank.model.Account;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByAccountNumber(String accountNumber);

    /** Row lock (SELECT ... FOR UPDATE) so concurrent debits cannot overdraw an account. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Account a where a.accountNumber = :no")
    Optional<Account> findForUpdate(@Param("no") String accountNumber);

    /** Custom query: account listing with optional branch and account-number filters. */
    @Query("select a from Account a where (:branchId is null or a.branch.id = :branchId) "
         + "and (:q is null or a.accountNumber like concat('%', :q, '%'))")
    Page<Account> search(@Param("branchId") Long branchId, @Param("q") String q, Pageable pageable);

    /** Custom query: active accounts whose balance is below a threshold. */
    @Query("select a from Account a where a.balance < :threshold and a.status = :status order by a.balance")
    List<Account> findLowBalance(@Param("threshold") BigDecimal threshold, @Param("status") Account.Status status);

    /** Custom query: top-N depositors = customers ranked by total balance over all their accounts. */
    @Query("select c.fullName, c.pan, sum(a.balance) from Account a join a.holders c "
         + "group by c.id, c.fullName, c.pan order by sum(a.balance) desc")
    List<Object[]> topDepositors(Pageable pageable);

    /** Custom query: branch name, number of accounts and total deposits. */
    @Query("select b.name, count(a), sum(a.balance) from Account a join a.branch b "
         + "group by b.id, b.name order by sum(a.balance) desc")
    List<Object[]> branchTotals();

    /** Custom query: total deposits held by the bank. */
    @Query("select sum(a.balance) from Account a")
    BigDecimal totalDeposits();
}
