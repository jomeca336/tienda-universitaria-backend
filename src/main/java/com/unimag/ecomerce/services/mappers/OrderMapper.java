package com.unimag.ecomerce.services.mappers;

import com.unimag.ecomerce.api.dto.OrderDTO;
import com.unimag.ecomerce.domine.entities.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "shippingAddressId", source = "shippingAddress.id")
    OrderDTO.OrderResponse toDTO(Order order);

}
