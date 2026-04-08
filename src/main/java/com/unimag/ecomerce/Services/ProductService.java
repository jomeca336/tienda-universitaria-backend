package com.unimag.ecomerce.services;

import com.unimag.ecomerce.dto.ProductDTO;
import com.unimag.ecomerce.entities.Product;

import java.util.List;

public interface ProductService {

    ProductDTO.ProductResponse create(ProductDTO.CreateProductRequest request);
    ProductDTO.ProductResponse update(Long id, ProductDTO.UpdateProductRequest request);
    ProductDTO.ProductResponse get(Long id);
    Product getObjectById(Long id);
    List<ProductDTO.ProductResponse> list();
    void delete(Long id);
}
