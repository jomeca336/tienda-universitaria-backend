package com.unimag.ecomerce.services;

import com.unimag.ecomerce.api.dto.InventoryDTO;
import com.unimag.ecomerce.domine.entities.Inventory;
import com.unimag.ecomerce.domine.repositories.InventoryRepository;
import com.unimag.ecomerce.domine.repositories.ProductRepository;
import com.unimag.ecomerce.exception.ResourceNotFoundException;
import com.unimag.ecomerce.exception.ValidationException;
import com.unimag.ecomerce.services.mappers.InventoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository repository;
    private final ProductRepository productRepository;
    private final InventoryMapper mapper;

    @Override
    public InventoryDTO.InventoryResponse update(Long productId, InventoryDTO.UpdateInventoryRequest request) {
        if (request.stock() != null && request.stock() < 0) {
            throw new ValidationException("El stock no puede ser negativo");
        }
        if (request.minStock() != null && request.minStock() < 0) {
            throw new ValidationException("El stock mínimo no puede ser negativo");
        }
        Inventory inventory = getInventoryByProductId(productId);
        mapper.updateEntity(request, inventory);
        return mapper.toDTO(repository.save(inventory));
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryDTO.InventoryResponse getByProductId(Long productId) {
        return mapper.toDTO(getInventoryByProductId(productId));
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Inventory not found with id: " + id);
        }
        repository.deleteById(id);
    }

    private Inventory getInventoryByProductId(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product not found with id: " + productId);
        }
        return repository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product id: " + productId));
    }
}
