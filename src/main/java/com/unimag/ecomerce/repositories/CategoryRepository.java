package com.unimag.ecomerce.repositories;

import com.unimag.ecomerce.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
