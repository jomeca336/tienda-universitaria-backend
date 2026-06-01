package com.unimag.ecomerce.services;

import com.unimag.ecomerce.api.dto.StockMovementDTO;
import com.unimag.ecomerce.domine.entities.Product;

import java.util.List;

public interface StockMovementService {
    void record(Product product, int quantity, String type, int stockAfter);
    List<StockMovementDTO.StockMovementResponse> getByProduct(Long productId);
}
