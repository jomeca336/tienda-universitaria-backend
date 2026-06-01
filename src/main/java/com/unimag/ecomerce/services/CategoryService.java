package com.unimag.ecomerce.services;

import com.unimag.ecomerce.api.dto.CategoryDTO;

import java.util.List;

public interface CategoryService {

    CategoryDTO.CategoryResponse create(CategoryDTO.CreateCategoryRequest request);
    CategoryDTO.CategoryResponse get(Long id);
    List<CategoryDTO.CategoryResponse> list();
    void delete(Long id);
}
