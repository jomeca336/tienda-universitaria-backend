package com.unimag.ecomerce.services;


import com.unimag.ecomerce.dto.AddressDTO;
import com.unimag.ecomerce.entities.Address;
import com.unimag.ecomerce.mappers.AddressMapper;
import com.unimag.ecomerce.exception.NotFoundException;
import com.unimag.ecomerce.repositories.AddressRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AddressServiceImpl implements AddressService {

    private final AddressRepository repository;
    private final AddressMapper mapper;

    @Override
    public AddressDTO.AddressResponse create(AddressDTO.CreateAddressRequest request) {
        Address entity = mapper.toEntity(request);
        return mapper.toDTO(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public AddressDTO.AddressResponse get(Long id) {
        return mapper.toDTO(getObjectById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Address getObjectById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Address not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressDTO.AddressResponse> list() {
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Address not found");
        }
        repository.deleteById(id);
    }
}

