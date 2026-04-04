package com.unimag.ecomerce.repositories;

import com.unimag.ecomerce.entities.Category;
import com.unimag.ecomerce.entities.Inventory;
import com.unimag.ecomerce.entities.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InventoryRepositoryIntegrationTest extends AbstractRepositoryIT {

    @Autowired
    private InventoryRepository inventoryRepo;

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private CategoryRepository categoryRepo;

    @Test
    @DisplayName("Debe encontrar todos los productos cuyo stock se encuentre por debajo del stock mínimo")
    void shouldFindProductsWithLowStock() {
        // Arrange
        Category cat = new Category();
        cat.setName("Hogar");
        categoryRepo.save(cat);

        Product p1 = new Product();
        p1.setSku("HOG-001");
        p1.setName("Silla");
        p1.setPrice(30.0);
        p1.setActive(true);
        p1.setCategory(cat);
        // Generamos un inventario con stock crítico
        Inventory inv1 = new Inventory();
        inv1.setProduct(p1);
        inv1.setStock(2);
        inv1.setMinStock(5);
        p1.setInventory(inv1);
        productRepo.save(p1);

        Product p2 = new Product();
        p2.setSku("HOG-002");
        p2.setName("Mesa");
        p2.setPrice(100.0);
        p2.setActive(true);
        p2.setCategory(cat);
        // Generamos un inventario con stock saludable
        Inventory inv2 = new Inventory();
        inv2.setProduct(p2);
        inv2.setStock(10);
        inv2.setMinStock(5);
        p2.setInventory(inv2);
        productRepo.save(p2);

        // Act
        // La consulta de bajo stock lógicamente recae en los productos involucrando Inventory
        List<Product> lowStockProducts = productRepo.findProductsWithLowStock();

        // Assert
        assertThat(lowStockProducts).hasSize(1);
        assertThat(lowStockProducts.get(0).getSku()).isEqualTo("HOG-001");
        assertThat(lowStockProducts.get(0).getInventory().getStock()).isEqualTo(2);
    }
}
