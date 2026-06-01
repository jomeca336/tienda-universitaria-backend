package com.unimag.ecomerce.domine.repositories;

import com.unimag.ecomerce.domine.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySku(String sku);

    List<Product> findByDeletedFalseOrderByIdAsc();

    List<Product> findByDeletedTrueOrderByIdAsc();

    List<Product> findByActiveTrueAndCategoryId(Long categoryId);

    @Query("SELECT p FROM Product p WHERE p.deleted = false AND p.inventory.stock <= p.inventory.minStock")
    List<Product> findProductsWithLowStock();
}