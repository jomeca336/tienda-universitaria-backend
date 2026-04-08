package com.unimag.ecomerce.services;

import com.unimag.ecomerce.dto.CustomerDTO;
import com.unimag.ecomerce.entities.Customer;

import java.util.List;

public interface CustomerService {

    CustomerDTO.CustomerResponse create(CustomerDTO.CreateCustomerRequest request);
    CustomerDTO.CustomerResponse update(Long id, CustomerDTO.UpdateCustomerRequest request);
    CustomerDTO.CustomerResponse get(Long id);
    Customer getObjectById(Long id);
    List<CustomerDTO.CustomerResponse> list();
    void delete(Long id);
}
