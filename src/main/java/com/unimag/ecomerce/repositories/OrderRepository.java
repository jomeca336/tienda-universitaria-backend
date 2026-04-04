package com.unimag.ecomerce.repositories;

import com.unimag.ecomerce.entities.Order;
import com.unimag.ecomerce.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByCustomerId(Long customerId);


    @Query("SELECT o FROM Order o WHERE o.customer.id = :customerId " +
            "AND o.status = :status " +
            "AND o.orderDate BETWEEN :startDate AND :endDate " +
            "AND o.total BETWEEN :minTotal AND :maxTotal")
    List<Order> findOrdersByFilters(Long customerId, OrderStatus status,
                                    Instant startDate, Instant endDate,
                                    Double minTotal, Double maxTotal);

    @Query("SELECT FUNCTION('date_trunc', 'month', o.orderDate) as month, SUM(o.total) " +
            "FROM Order o WHERE o.status = 'PAID' GROUP BY month")
    List<Object[]> getMonthlyIncome();
}
