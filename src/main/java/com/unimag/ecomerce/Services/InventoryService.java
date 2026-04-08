package com.unimag.ecomerce.Services;

import com.unimag.ecomerce.dto.InventoryDTO;

public interface InventoryService {

    InventoryDTO.InventoryResponse update(Long productId, InventoryDTO.UpdateInventoryRequest request);
    InventoryDTO.InventoryResponse getByProductId(Long productId);
}
