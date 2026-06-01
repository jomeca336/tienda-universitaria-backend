package com.unimag.ecomerce.domine.repositories;

import com.unimag.ecomerce.domine.entities.OrderStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory, Long> {

    List<OrderStatusHistory> findByOrderIdOrderByChangeDateDesc(Long orderId);
}