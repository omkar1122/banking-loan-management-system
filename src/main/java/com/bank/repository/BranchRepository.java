package com.bank.repository;

import com.bank.model.Branch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BranchRepository extends JpaRepository<Branch, Long> {
    boolean existsByIfscCode(String ifscCode);

    /** Custom query: branch search by name, city or IFSC. */
    @Query("select b from Branch b where :q is null or lower(b.name) like lower(concat('%', :q, '%')) "
         + "or lower(b.city) like lower(concat('%', :q, '%')) or b.ifscCode like upper(concat('%', :q, '%'))")
    Page<Branch> search(@Param("q") String q, Pageable pageable);
}
