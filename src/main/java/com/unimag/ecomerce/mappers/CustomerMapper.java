package com.unimag.ecomerce.mappers;

import com.unimag.ecomerce.dto.CustomerDTO;
import com.unimag.ecomerce.entities.Customer;
import com.unimag.ecomerce.enums.CustomerStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper (componentModel = "spring")
public interface CustomerMapper {

    //Esto es para el Create
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "addresses", ignore = true)
    @Mapping(target = "status", constant = "ACTIVE")
    Customer toEntity(CustomerDTO.CreateCustomerRequest request);

    //Esto es para el Update notas para mi y no perderme

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "addresses", ignore = true)
    void updateEntity(CustomerDTO.UpdateCustomerRequest request, @MappingTarget Customer customer);

    CustomerDTO.CustomerResponse toDTO(Customer customer);

}
