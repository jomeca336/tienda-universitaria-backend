package com.unimag.ecomerce.dto;

import java.io.Serializable;

public class InventoryDTO {

    private record UpdateInventoryRequest(
            Integer stock,
            Integer minStock

    ) implements Serializable {}

    private record InventoryResponse(
            Long id,
            Integer stock,
            Integer minStock

    ) implements Serializable{}
}
