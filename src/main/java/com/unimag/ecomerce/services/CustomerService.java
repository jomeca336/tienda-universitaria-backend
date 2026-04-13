package com.unimag.ecomerce.services;

import com.unimag.ecomerce.api.dto.CustomerDTO.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface CustomerService {

    CustomerResponse create(CreateCustomerRequest request);
    CustomerResponse get(Long id);
    List<CustomerResponse> list();
    CustomerResponse update(Long id, UpdateCustomerRequest request);
}
