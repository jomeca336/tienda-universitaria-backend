package com.unimag.ecomerce.Services;

import com.unimag.ecomerce.dto.ProductDTO;
import com.unimag.ecomerce.entities.Inventory;
import com.unimag.ecomerce.entities.Product;
import com.unimag.ecomerce.exception.NotFoundException;
import com.unimag.ecomerce.mappers.ProductMapper;
import com.unimag.ecomerce.repositories.InventoryRepository;
import com.unimag.ecomerce.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;
    private final CategoryService categoryService;
    private final InventoryRepository inventoryRepository;
    private final ProductMapper mapper;

    @Override
    public ProductDTO.ProductResponse create(ProductDTO.CreateProductRequest request) {
        if (request.price() == null || request.price() <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a cero");
        }
        Product entity = mapper.toEntity(request);
        entity.setCategory(categoryService.getObjectById(request.categoryId()));
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
        Product product = getObjectById(id);
        mapper.updateEntity(request, product);
        if (request.categoryId() != null) {
            product.setCategory(categoryService.getObjectById(request.categoryId()));
        }
        return mapper.toDTO(repository.save(product));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDTO.ProductResponse get(Long id) {
        return mapper.toDTO(getObjectById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Product getObjectById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO.ProductResponse> list() {
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Product not found");
        }
        repository.deleteById(id);
    }
}
