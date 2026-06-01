package com.unimag.ecomerce.services.mappers;

import com.unimag.ecomerce.api.dto.OrderDTO;
import com.unimag.ecomerce.domine.entities.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class})
public interface OrderMapper {
    @Mapping(target = "customerId",          source = "customer.id")
    @Mapping(target = "customerName",        source = "customer.name")
    @Mapping(target = "shippingAddressId",   source = "shippingAddress.id")
    @Mapping(target = "shippingAddressLine", source = "shippingAddress.addressLine")
    @Mapping(target = "shippingCity",        source = "shippingAddress.city")
    @Mapping(target = "items",               source = "items")
    OrderDTO.OrderResponse toDTO(Order order);
}
