package com.unimag.ecomerce.dto;

import java.io.Serializable;

public class OrderItemDTO {

    public record CreateOrderItemRequest(
        Long productId,
        Integer quantity
    ) implements Serializable{}

    public record OrderItemResponse(
            Long id,
            Long productId,
            Integer quantity,
            Double unitPrice,
            Double subtotal
    ) implements Serializable {}
}
