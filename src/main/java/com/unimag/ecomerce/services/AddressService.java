package com.unimag.ecomerce.services;

import com.unimag.ecomerce.api.dto.AddressDTO;
import com.unimag.ecomerce.domine.entities.Address;

import java.util.List;

public interface AddressService {

    AddressDTO.AddressResponse create(AddressDTO.CreateAddressRequest request);
    AddressDTO.AddressResponse get(Long id);
    Address getObjectById(Long id);
    List<AddressDTO.AddressResponse> list();
    void delete(Long id);
}
