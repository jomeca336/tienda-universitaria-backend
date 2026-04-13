package com.unimag.ecomerce.services;

import com.unimag.ecomerce.api.dto.InventoryDTO;
import com.unimag.ecomerce.api.dto.ProductDTO;
import com.unimag.ecomerce.domine.entities.Product;

import java.util.List;

public interface ProductService {

    ProductDTO.ProductResponse create(ProductDTO.CreateProductRequest request);
    ProductDTO.ProductResponse get(Long id);
    List<ProductDTO.ProductResponse> list();
    ProductDTO.ProductResponse update(Long id, ProductDTO.UpdateProductRequest request);

    ProductDTO.ProductResponse updateInventory(Long id, InventoryDTO.UpdateInventoryRequest request);
    Product getObjectById(Long id);

    void delete(Long id);


}
