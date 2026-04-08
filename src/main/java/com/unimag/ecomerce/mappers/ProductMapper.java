package com.unimag.ecomerce.mappers;

import com.unimag.ecomerce.dto.ProductDTO;
import com.unimag.ecomerce.entities.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "categoryId", source = "category.id")
    ProductDTO.ProductResponse toDTO(Product product);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "inventory", ignore = true)
    Product toEntity(ProductDTO.CreateProductRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "inventory", ignore = true)
    void updateEntity(ProductDTO.UpdateProductRequest request, @MappingTarget Product product);
}
