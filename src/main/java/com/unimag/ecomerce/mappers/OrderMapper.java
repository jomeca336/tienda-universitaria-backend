package com.unimag.ecomerce.mappers;

import com.unimag.ecomerce.dto.OrderDTO;
import com.unimag.ecomerce.entities.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "shippingAddressId", source = "shippingAddress.id")
    OrderDTO.OrderResponse toDTO(Order order);

}
