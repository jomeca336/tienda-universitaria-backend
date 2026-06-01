package com.unimag.ecomerce.services;

import com.unimag.ecomerce.api.dto.CustomerDTO.*;

import java.util.List;


public interface CustomerService {

    CustomerResponse create(CreateCustomerRequest request);
    CustomerResponse get(Long id);
    List<CustomerResponse> list();
    List<CustomerResponse> listDeleted();
    CustomerResponse update(Long id, UpdateCustomerRequest request);
    void delete(Long id);
    CustomerResponse restore(Long id);
}
