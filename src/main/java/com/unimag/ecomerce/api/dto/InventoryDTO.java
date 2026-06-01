package com.unimag.ecomerce.api.dto;

import java.io.Serializable;

public class InventoryDTO {

    public record UpdateInventoryRequest(
            Integer stock,
            Integer minStock

    ) implements Serializable {}

    public record InventoryResponse(
            Long id,
            Integer stock,
            Integer minStock

    ) implements Serializable{}
}
