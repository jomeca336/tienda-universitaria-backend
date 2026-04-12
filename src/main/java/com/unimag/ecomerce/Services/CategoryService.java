package com.unimag.ecomerce.services;

import com.unimag.ecomerce.api.dto.CategoryDTO;
import com.unimag.ecomerce.domine.entities.Category;

import java.util.List;

public interface CategoryService {

    CategoryDTO.CategoryResponse create(CategoryDTO.CreateCategoryRequest request);
    CategoryDTO.CategoryResponse get(Long id);
    Category getObjectById(Long id);
    List<CategoryDTO.CategoryResponse> list();
    void delete(Long id);
}
