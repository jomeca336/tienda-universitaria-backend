package com.unimag.ecomerce.services;

import com.unimag.ecomerce.api.dto.AddressDTO;

import java.util.List;

public interface AddressService {

    AddressDTO.AddressResponse create(Long customerId, AddressDTO.CreateAddressRequest request);
    AddressDTO.AddressResponse get(Long id);
    List<AddressDTO.AddressResponse> list(Long customerId);
    void delete(Long id);
}
