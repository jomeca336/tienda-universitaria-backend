package com.unimag.ecomerce.services.mappers;

import com.unimag.ecomerce.api.dto.CategoryDTO;
import com.unimag.ecomerce.domine.entities.Category;
import com.unimag.ecomerce.services.mappers.CategoryMapper;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import static org.assertj.core.api.Assertions.assertThat;
public class CategoryMapperTest {

    private final CategoryMapper mapper= Mappers.getMapper(CategoryMapper.class);

    @Test
    void toEntity_ShouldMapFields(){
        var req= new CategoryDTO.CreateCategoryRequest("Tecnologia");

        Category entity = mapper.toEntity(req);

        assertThat(entity.getName()).isEqualTo("Tecnologia");
        assertThat(entity.getId()).isNull();
        assertThat(entity.getProducts()).isNull();
    }

    @Test
    void toDTO_ShouldMapFields() {
        Category category = Category.builder()
                .id(1L)
                .name("Tecnologia")
                .build();

        var response = mapper.toDTO(category);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Tecnologia");
    }
}

