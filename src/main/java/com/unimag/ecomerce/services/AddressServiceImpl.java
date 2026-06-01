package com.unimag.ecomerce.services;

import com.unimag.ecomerce.api.dto.AddressDTO.*;
import com.unimag.ecomerce.domine.entities.Address;
import com.unimag.ecomerce.domine.entities.Customer;
import com.unimag.ecomerce.domine.repositories.AddressRepository;
import com.unimag.ecomerce.domine.repositories.CustomerRepository;
import com.unimag.ecomerce.exception.ResourceNotFoundException;
import com.unimag.ecomerce.services.mappers.AddressMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AddressServiceImpl implements AddressService {

    private final AddressRepository repository;
    private final CustomerRepository customerRepository;
    private final AddressMapper mapper;

    @Override
    public AddressResponse create(Long customerId, CreateAddressRequest request) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));
        Address entity = mapper.toEntity(request);
        entity.setCustomer(customer);
        return mapper.toDTO(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public AddressResponse get(Long id) {
        return mapper.toDTO(getAddressById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressResponse> list(Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found with id: " + customerId);
        }
        return repository.findByCustomerId(customerId).stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Address not found with id: " + id);
        }
        repository.deleteById(id);
    }

    private Address getAddressById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + id));
    }
}
