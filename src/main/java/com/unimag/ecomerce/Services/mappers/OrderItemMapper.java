package com.unimag.ecomerce.services.mappers;

import com.unimag.ecomerce.api.dto.OrderItemDTO;
import com.unimag.ecomerce.domine.entities.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(target = "productId", source = "product.id")
    OrderItemDTO.OrderItemResponse toDTO(OrderItem item);
}
