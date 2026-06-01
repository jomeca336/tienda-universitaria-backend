package com.unimag.ecomerce.services.mappers;

import com.unimag.ecomerce.api.dto.ProductDTO;
import com.unimag.ecomerce.domine.entities.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "stock",    source = "inventory.stock")
    @Mapping(target = "minStock", source = "inventory.minStock")
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
