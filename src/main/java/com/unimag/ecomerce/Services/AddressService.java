package com.unimag.ecomerce.services;

import com.unimag.ecomerce.dto.AddressDTO;
import com.unimag.ecomerce.entities.Address;

import java.util.List;

public interface AddressService {

    AddressDTO.AddressResponse create(AddressDTO.CreateAddressRequest request);
    AddressDTO.AddressResponse get(Long id);
    Address getObjectById(Long id);
    List<AddressDTO.AddressResponse> list();
    void delete(Long id);
}
