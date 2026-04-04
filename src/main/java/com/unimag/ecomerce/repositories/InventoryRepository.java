package com.unimag.ecomerce.repositories;

import com.unimag.ecomerce.entities.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
}
