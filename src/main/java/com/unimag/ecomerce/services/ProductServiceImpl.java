package com.unimag.ecomerce.services;

import com.unimag.ecomerce.api.dto.InventoryDTO;
import com.unimag.ecomerce.api.dto.ProductDTO;
import com.unimag.ecomerce.domine.entities.Inventory;
import com.unimag.ecomerce.domine.entities.Product;
import com.unimag.ecomerce.domine.repositories.InventoryRepository;
import com.unimag.ecomerce.domine.repositories.ProductRepository;
import com.unimag.ecomerce.exception.ConflictException;
import com.unimag.ecomerce.exception.ResourceNotFoundException;
import com.unimag.ecomerce.exception.ValidationException;
import com.unimag.ecomerce.services.mappers.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;
    private final CategoryServiceImpl categoryService;
    private final InventoryRepository inventoryRepository;
    private final ProductMapper mapper;

    @Override
    public ProductDTO.ProductResponse create(ProductDTO.CreateProductRequest request) {
        if (request.price() == null || request.price() <= 0) {
            throw new ValidationException("El precio debe ser mayor a cero");
        }
        if (repository.findBySku(request.sku()).isPresent()) {
            throw new ConflictException("Ya existe un producto con el SKU: " + request.sku());
        }
        Product entity = mapper.toEntity(request);
        entity.setCategory(categoryService.getCategoryById(request.categoryId()));
        entity.setActive(true);
        Product saved = repository.save(entity);
        Inventory inventory = Inventory.builder()
                .product(saved)
                .stock(0)
                .minStock(0)
                .build();
        inventoryRepository.save(inventory);
        return mapper.toDTO(saved);
    }

    @Override
    public ProductDTO.ProductResponse update(Long id, ProductDTO.UpdateProductRequest request) {
        Product product = getProductById(id);
        mapper.updateEntity(request, product);
        if (request.categoryId() != null) {
            product.setCategory(categoryService.getCategoryById(request.categoryId()));
        }
        return mapper.toDTO(repository.save(product));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDTO.ProductResponse get(Long id) {
        return mapper.toDTO(getProductById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO.ProductResponse> list() {
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public ProductDTO.ProductResponse updateInventory(Long id, InventoryDTO.UpdateInventoryRequest request) {
        if (request.stock() == null || request.stock() < 0) {
            throw new ValidationException("El stock no puede ser negativo");
        }
        if (request.minStock() == null || request.minStock() < 0) {
            throw new ValidationException("El stock mínimo no puede ser negativo");
        }
        Product product = getProductById(id);
        Inventory inventory = inventoryRepository.findByProductId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product id: " + id));
        inventory.setStock(request.stock());
        inventory.setMinStock(request.minStock());
        inventoryRepository.save(inventory);
        return mapper.toDTO(product);
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        repository.deleteById(id);
    }

    Product getProductById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }
}
