package com.unimag.ecomerce.services;

import com.unimag.ecomerce.dto.ProductDTO;
import com.unimag.ecomerce.entities.Category;
import com.unimag.ecomerce.entities.Inventory;
import com.unimag.ecomerce.entities.Product;
import com.unimag.ecomerce.mappers.ProductMapper;
import com.unimag.ecomerce.repositories.InventoryRepository;
import com.unimag.ecomerce.repositories.ProductRepository;
import com.unimag.ecomerce.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock private ProductRepository productRepository;
    @Mock private CategoryService categoryService;
    @Mock private InventoryRepository inventoryRepository;
    @Mock private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    // ─── create ───────────────────────────────────────────────────────────────

    @Test
    void create_ShouldSetCategoryAndSaveProduct() {
        var request = new ProductDTO.CreateProductRequest("SKU1", "Cuaderno", "Desc", 5000.0, true, 1L);
        Category category = Category.builder().id(1L).name("Papelería").build();
        Product entity = Product.builder().sku("SKU1").name("Cuaderno").price(5000.0).active(true).build();
        Product saved = Product.builder().id(10L).sku("SKU1").name("Cuaderno").price(5000.0).active(true).category(category).build();
        var expectedResponse = new ProductDTO.ProductResponse(10L, 1L, "SKU1", "Cuaderno", "Desc", 5000.0, true);

        when(productMapper.toEntity(request)).thenReturn(entity);
        when(categoryService.getObjectById(1L)).thenReturn(category);
        when(productRepository.save(any())).thenReturn(saved);
        when(productMapper.toDTO(saved)).thenReturn(expectedResponse);

        ProductDTO.ProductResponse response = productService.create(request);

        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.categoryId()).isEqualTo(1L);
        assertThat(response.sku()).isEqualTo("SKU1");
        assertThat(entity.getCategory()).isEqualTo(category);
    }

    @Test
    void create_ShouldInitializeInventoryWithZeroStock() {
        var request = new ProductDTO.CreateProductRequest("SKU1", "Cuaderno", "Desc", 5000.0, true, 1L);
        Category category = Category.builder().id(1L).build();
        Product entity = Product.builder().sku("SKU1").build();
        Product saved = Product.builder().id(10L).sku("SKU1").build();

        when(productMapper.toEntity(request)).thenReturn(entity);
        when(categoryService.getObjectById(1L)).thenReturn(category);
        when(productRepository.save(any())).thenReturn(saved);
        when(productMapper.toDTO(saved)).thenReturn(mock(ProductDTO.ProductResponse.class));

        productService.create(request);

        ArgumentCaptor<Inventory> inventoryCaptor = ArgumentCaptor.forClass(Inventory.class);
        verify(inventoryRepository).save(inventoryCaptor.capture());
        assertThat(inventoryCaptor.getValue().getStock()).isEqualTo(0);
        assertThat(inventoryCaptor.getValue().getMinStock()).isEqualTo(0);
        assertThat(inventoryCaptor.getValue().getProduct()).isEqualTo(saved);
    }

    // ─── update ───────────────────────────────────────────────────────────────

    @Test
    void update_ShouldUpdateFieldsViaMapper() {
        Product product = Product.builder().id(1L).sku("OLD").name("Old").price(1000.0).build();
        var request = new ProductDTO.UpdateProductRequest("NEW", "New Name", "New Desc", 2000.0, true, null);
        var expectedResponse = new ProductDTO.ProductResponse(1L, null, "NEW", "New Name", "New Desc", 2000.0, true);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toDTO(product)).thenReturn(expectedResponse);

        ProductDTO.ProductResponse response = productService.update(1L, request);

        verify(productMapper).updateEntity(request, product);
        assertThat(response.sku()).isEqualTo("NEW");
    }

    @Test
    void update_ShouldChangeCategory_WhenCategoryIdProvided() {
        Product product = Product.builder().id(1L).build();
        Category newCategory = Category.builder().id(5L).name("Nueva Cat").build();
        var request = new ProductDTO.UpdateProductRequest("SKU", "Name", "Desc", 1000.0, true, 5L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(categoryService.getObjectById(5L)).thenReturn(newCategory);
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toDTO(product)).thenReturn(mock(ProductDTO.ProductResponse.class));

        productService.update(1L, request);

        assertThat(product.getCategory()).isEqualTo(newCategory);
        verify(categoryService).getObjectById(5L);
    }

    // ─── get ──────────────────────────────────────────────────────────────────

    @Test
    void get_ShouldReturnProductResponse_WhenProductExists() {
        Product product = Product.builder().id(1L).sku("SKU1").name("Cuaderno").price(5000.0).build();
        var expectedResponse = new ProductDTO.ProductResponse(1L, null, "SKU1", "Cuaderno", null, 5000.0, null);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productMapper.toDTO(product)).thenReturn(expectedResponse);

        ProductDTO.ProductResponse response = productService.get(1L);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.sku()).isEqualTo("SKU1");
    }

    @Test
    void getObjectById_ShouldThrow_WhenProductNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getObjectById(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("99");
    }

    // ─── list ─────────────────────────────────────────────────────────────────

    @Test
    void list_ShouldReturnAllProductsAsDTOs() {
        Product p1 = Product.builder().id(1L).sku("A").build();
        Product p2 = Product.builder().id(2L).sku("B").build();
        var r1 = new ProductDTO.ProductResponse(1L, null, "A", null, null, null, null);
        var r2 = new ProductDTO.ProductResponse(2L, null, "B", null, null, null, null);

        when(productRepository.findAll()).thenReturn(List.of(p1, p2));
        when(productMapper.toDTO(p1)).thenReturn(r1);
        when(productMapper.toDTO(p2)).thenReturn(r2);

        List<ProductDTO.ProductResponse> result = productService.list();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(ProductDTO.ProductResponse::sku).containsExactly("A", "B");
    }

    // ─── delete ───────────────────────────────────────────────────────────────

    @Test
    void delete_ShouldDeleteProduct_WhenProductExists() {
        when(productRepository.existsById(1L)).thenReturn(true);

        productService.delete(1L);

        verify(productRepository).deleteById(1L);
    }

    @Test
    void delete_ShouldThrow_WhenProductNotFound() {
        when(productRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> productService.delete(99L))
                .isInstanceOf(NotFoundException.class);

        verify(productRepository, never()).deleteById(any());
    }
}
