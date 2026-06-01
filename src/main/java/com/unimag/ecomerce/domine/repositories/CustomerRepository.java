package com.unimag.ecomerce.domine.repositories;

import com.unimag.ecomerce.domine.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    List<Customer> findByDeletedFalseOrderByIdAsc();

    List<Customer> findByDeletedTrueOrderByIdAsc();

    @Query("SELECT o.customer, SUM(o.total) as totalSpent FROM Order o " +
            "GROUP BY o.customer ORDER BY totalSpent DESC")
    List<Object[]> findTopCustomers();
}
