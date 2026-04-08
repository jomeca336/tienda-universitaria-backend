package com.unimag.ecomerce.services;

import com.unimag.ecomerce.dto.InventoryDTO;
import com.unimag.ecomerce.entities.Inventory;
import com.unimag.ecomerce.exception.NotFoundException;
import com.unimag.ecomerce.mappers.InventoryMapper;
import com.unimag.ecomerce.repositories.InventoryRepository;
import com.unimag.ecomerce.repositories.ProductRepository;
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
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
        if (request.minStock() != null && request.minStock() < 0) {
            throw new IllegalArgumentException("El stock mínimo no puede ser negativo");
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

    private Inventory getInventoryByProductId(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new NotFoundException("Product not found with id: " + productId);
        }
        return repository.findByProductId(productId)
                .orElseThrow(() -> new NotFoundException("Inventory not found for product id: " + productId));
    }
}
