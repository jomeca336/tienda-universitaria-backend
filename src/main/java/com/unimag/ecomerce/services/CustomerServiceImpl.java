package com.unimag.ecomerce.services;

import com.unimag.ecomerce.api.dto.CustomerDTO;
import com.unimag.ecomerce.domine.entities.Customer;
import com.unimag.ecomerce.domine.repositories.CustomerRepository;
import com.unimag.ecomerce.exception.ResourceNotFoundException;
import com.unimag.ecomerce.services.mappers.CustomerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository repository;
    private final CustomerMapper mapper;

    @Override
    public CustomerDTO.CustomerResponse create(CustomerDTO.CreateCustomerRequest request) {
        Customer entity = mapper.toEntity(request);
        return mapper.toDTO(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerDTO.CustomerResponse get(Long id) {
        return mapper.toDTO(getCustomerById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerDTO.CustomerResponse> list() {
        return repository.findByDeletedFalseOrderByIdAsc().stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerDTO.CustomerResponse> listDeleted() {
        return repository.findByDeletedTrueOrderByIdAsc().stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public CustomerDTO.CustomerResponse update(Long id, CustomerDTO.UpdateCustomerRequest request) {
        Customer customer = getCustomerById(id);
        mapper.updateEntity(request, customer);
        return mapper.toDTO(repository.save(customer));
    }

    @Override
    public void delete(Long id) {
        Customer customer = getCustomerById(id);
        customer.setDeleted(true);
        repository.save(customer);
    }

    @Override
    public CustomerDTO.CustomerResponse restore(Long id) {
        Customer customer = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        customer.setDeleted(false);
        return mapper.toDTO(repository.save(customer));
    }

    Customer getCustomerById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
    }
}
