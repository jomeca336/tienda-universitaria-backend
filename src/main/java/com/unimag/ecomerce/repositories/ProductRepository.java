package com.unimag.ecomerce.repositories;

import com.unimag.ecomerce.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySku(String sku);


    List<Product> findByActiveTrueAndCategoryId(Long categoryId);


    @Query("SELECT p FROM Product p WHERE p.inventory.stock < p.inventory.minStock")
    List<Product> findProductsWithLowStock();
}