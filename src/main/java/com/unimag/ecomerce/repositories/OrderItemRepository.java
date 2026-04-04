package com.unimag.ecomerce.repositories;

import com.unimag.ecomerce.entities.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("SELECT oi.product, SUM(oi.quantity) as totalSold " +
           "FROM OrderItem oi " +
           "GROUP BY oi.product " +
           "ORDER BY totalSold DESC")
    List<Object[]> findTopSellingProducts();
}
