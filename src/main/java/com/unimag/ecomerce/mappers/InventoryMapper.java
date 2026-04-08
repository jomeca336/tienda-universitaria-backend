package com.unimag.ecomerce.mappers;

import com.unimag.ecomerce.dto.InventoryDTO;
import com.unimag.ecomerce.entities.Inventory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface InventoryMapper {

    InventoryDTO.InventoryResponse toDTO(Inventory inventory);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    void updateEntity(InventoryDTO.UpdateInventoryRequest request, @MappingTarget Inventory inventory);
}
