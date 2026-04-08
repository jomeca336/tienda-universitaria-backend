package com.unimag.ecomerce.mappers;

import com.unimag.ecomerce.dto.OrderItemDTO;
import com.unimag.ecomerce.entities.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(target = "productId", source = "product.id")
    OrderItemDTO.OrderItemResponse toDTO(OrderItem item);
}
