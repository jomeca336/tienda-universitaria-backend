package com.unimag.ecomerce.Services.mappers;


import com.unimag.ecomerce.dto.ProductDTO;
import com.unimag.ecomerce.entities.Category;
import com.unimag.ecomerce.entities.Product;
import com.unimag.ecomerce.mappers.ProductMapper;
import org.mapstruct.factory.Mappers;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductMapperTest {
    private final ProductMapper mapper = Mappers.getMapper(ProductMapper.class);

    @Test
    void toDTO_ShouldMapFields() {
        Category category = Category.builder()
                .id(1L)
                .build();

        Product product = Product.builder()
                .id(10L)
                .sku("SKU123")
                .name("Cuaderno")
                .description("Descripción")
                .price(5000.0)
                .active(true)
                .category(category)
                .build();

        ProductDTO.ProductResponse response = mapper.toDTO(product);

        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.categoryId()).isEqualTo(1L);
        assertThat(response.sku()).isEqualTo("SKU123");
        assertThat(response.name()).isEqualTo("Cuaderno");
        assertThat(response.description()).isEqualTo("Descripción");
        assertThat(response.price()).isEqualTo(5000.0);
        assertThat(response.active()).isTrue();
    }

    @Test
    void updateEntity_ShouldUpdateFields() {
        Product product = Product.builder()
                .id(1L)
                .sku("OLD")
                .name("Old Name")
                .description("Old Desc")
                .price(1000.0)
                .active(false)
                .build();

        var request = new ProductDTO.UpdateProductRequest("NEW", "New Name", "New Desc", 2000.0, true, 2L);

        mapper.updateEntity(request, product);

        assertThat(product.getSku()).isEqualTo("NEW");
        assertThat(product.getName()).isEqualTo("New Name");
        assertThat(product.getDescription()).isEqualTo("New Desc");
        assertThat(product.getPrice()).isEqualTo(2000.0);
        assertThat(product.getActive()).isTrue();
        assertThat(product.getId()).isEqualTo(1L);
        assertThat(product.getCategory()).isNull();
        assertThat(product.getInventory()).isNull();
    }

}
