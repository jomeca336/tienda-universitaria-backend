package com.unimag.ecomerce.services;

import com.unimag.ecomerce.api.dto.CustomerDTO.*;
import com.unimag.ecomerce.domine.entities.Customer;


import java.util.List;


public interface CustomerService {

    CustomerResponse create(CreateCustomerRequest request);
    CustomerResponse get(Long id);
    List<CustomerResponse> list();
    CustomerResponse update(Long id, UpdateCustomerRequest request);
    void delete(Long id);

    Customer getObjectById(Long id);
}
