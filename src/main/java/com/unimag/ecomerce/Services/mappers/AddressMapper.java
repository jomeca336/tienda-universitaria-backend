package com.unimag.ecomerce.services.mappers;

import com.unimag.ecomerce.api.dto.AddressDTO;
import com.unimag.ecomerce.domine.entities.Address;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customer", ignore = true)

    Address toEntity(AddressDTO.CreateAddressRequest request);

    AddressDTO.AddressResponse toDTO(Address address);

}
