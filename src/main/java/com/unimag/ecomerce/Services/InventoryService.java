package com.unimag.ecomerce.services;

import com.unimag.ecomerce.api.dto.InventoryDTO;

public interface InventoryService {

    InventoryDTO.InventoryResponse update(Long productId, InventoryDTO.UpdateInventoryRequest request);
    InventoryDTO.InventoryResponse getByProductId(Long productId);
}
