package com.unimag.ecomerce.services;

import com.unimag.ecomerce.api.dto.InventoryDTO.*;

public interface InventoryService {

    InventoryResponse update(Long productId, UpdateInventoryRequest request);
    InventoryResponse getByProductId(Long productId);
    void delete(Long id);
}
