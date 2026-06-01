package com.unimag.ecomerce.services.mappers;

import com.unimag.ecomerce.api.dto.CategoryDTO;
import com.unimag.ecomerce.domine.entities.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "products", ignore = true)
    Category toEntity(CategoryDTO.CreateCategoryRequest request);

    CategoryDTO.CategoryResponse toDTO(Category category);

}
