package com.unimag.ecomerce.services;

import com.unimag.ecomerce.api.dto.CustomerDTO;
import com.unimag.ecomerce.domine.entities.Customer;
import com.unimag.ecomerce.services.mappers.CustomerMapper;
import com.unimag.ecomerce.exception.NotFoundException;
import com.unimag.ecomerce.domine.repositories.CustomerRepository;
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
    public CustomerDTO.CustomerResponse update(Long id, CustomerDTO.UpdateCustomerRequest request) {

        Customer customer = getObjectById(id);

        mapper.updateEntity(request, customer);

        return mapper.toDTO(repository.save(customer));
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerDTO.CustomerResponse get(Long id) {
        return mapper.toDTO(getObjectById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Customer getObjectById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerDTO.CustomerResponse> list() {
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Customer not found");
        }
        repository.deleteById(id);
    }
}
