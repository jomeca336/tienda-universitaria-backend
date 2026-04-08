package com.unimag.ecomerce.services;

import com.unimag.ecomerce.dto.CategoryDTO;
import com.unimag.ecomerce.entities.Category;
import com.unimag.ecomerce.mappers.CategoryMapper;
import com.unimag.ecomerce.exception.NotFoundException;
import com.unimag.ecomerce.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repository;
    private final CategoryMapper mapper;

    @Override
    public CategoryDTO.CategoryResponse create(CategoryDTO.CreateCategoryRequest request) {
        Category entity = mapper.toEntity(request);
        return mapper.toDTO(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDTO.CategoryResponse get(Long id) {
        return mapper.toDTO(getObjectById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Category getObjectById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDTO.CategoryResponse> list() {
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Category not found");
        }
        repository.deleteById(id);
    }
}
