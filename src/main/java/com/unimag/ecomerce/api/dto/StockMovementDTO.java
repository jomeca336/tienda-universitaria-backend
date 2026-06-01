package com.unimag.ecomerce.api.dto;

import java.io.Serializable;
import java.time.Instant;

public class StockMovementDTO {

    public record StockMovementResponse(
            Long id,
            Long productId,
            String productName,
            Integer quantity,
            String type,
            Integer stockAfter,
            Instant date
    ) implements Serializable {}
}
