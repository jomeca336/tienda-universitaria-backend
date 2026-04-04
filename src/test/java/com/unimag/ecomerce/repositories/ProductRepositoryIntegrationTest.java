package com.unimag.ecomerce.repositories;

import com.unimag.ecomerce.entities.Category;
import com.unimag.ecomerce.entities.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ProductRepositoryIntegrationTest extends AbstractRepositoryIT {

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private CategoryRepository categoryRepo;

    @Test
    @DisplayName("Debido a que buscamos por categoría, debería encontrar los productos activos pertenecientes a esta")
    void shouldFindActiveProductsByCategory() {
        // Arrange
        Category cat = new Category();
        cat.setName("Electrónica");
        categoryRepo.save(cat);

        Product p1 = new Product();
        p1.setSku("ELEC-001");
        p1.setName("Smartphone");
        p1.setPrice(500.0);
        p1.setActive(true);
        p1.setCategory(cat);
        productRepo.save(p1);

        Product p2 = new Product();
        p2.setSku("ELEC-002");
        p2.setName("Laptop Antigal");
        p2.setPrice(100.0);
        p2.setActive(false); // No activo, no debería salir en la consulta final
        p2.setCategory(cat);
        productRepo.save(p2);

        // Act
        List<Product> byCategory = productRepo.findByActiveTrueAndCategoryId(cat.getId());

        // Assert
        assertThat(byCategory).hasSize(1);
        assertThat(byCategory.get(0).getSku()).isEqualTo("ELEC-001");
    }
}
