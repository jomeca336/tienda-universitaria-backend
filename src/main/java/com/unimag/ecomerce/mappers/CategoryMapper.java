package com.unimag.ecomerce.mappers;

import com.unimag.ecomerce.dto.CategoryDTO;
import com.unimag.ecomerce.entities.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "products", ignore = true)
    Category toEntity(CategoryDTO.CreateCategoryRequest request);

    CategoryDTO.CategoryResponse toDTO(Category category);

}
