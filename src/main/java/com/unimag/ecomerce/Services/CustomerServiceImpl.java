package com.unimag.ecomerce.Services;

import com.unimag.ecomerce.dto.CustomerDTO;
import com.unimag.ecomerce.entities.Customer;
import com.unimag.ecomerce.mappers.CustomerMapper;
import com.unimag.ecomerce.repositories.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
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
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with id: " + id));
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
            throw new EntityNotFoundException("Customer not found");
        }
        repository.deleteById(id);
    }
}
