package com.bank.repository;

import com.bank.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByPan(String pan);

    /** Custom query: customer search by name or PAN (null q returns everyone). */
    @Query("select c from Customer c where :q is null or lower(c.fullName) like lower(concat('%', :q, '%')) "
         + "or c.pan like upper(concat('%', :q, '%'))")
    Page<Customer> search(@Param("q") String q, Pageable pageable);
}
