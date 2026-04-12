package com.unimag.ecomerce.services;

import com.unimag.ecomerce.api.dto.CustomerDTO;
import com.unimag.ecomerce.domine.entities.Customer;

import java.util.List;

public interface CustomerService {

    CustomerDTO.CustomerResponse create(CustomerDTO.CreateCustomerRequest request);
    CustomerDTO.CustomerResponse update(Long id, CustomerDTO.UpdateCustomerRequest request);
    CustomerDTO.CustomerResponse get(Long id);
    Customer getObjectById(Long id);
    List<CustomerDTO.CustomerResponse> list();
    void delete(Long id);
}
