package com.unimag.ecomerce.domine.repositories;

import com.unimag.ecomerce.domine.entities.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    List<StockMovement> findByProductIdOrderByDateDesc(Long productId);
}
