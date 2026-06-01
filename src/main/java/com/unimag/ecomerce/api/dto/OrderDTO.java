package com.unimag.ecomerce.api.dto;


import com.unimag.ecomerce.enums.OrderStatus;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

public class OrderDTO {

    public record CreateOrderRequest(
        Long customerId,
        Long shippingAddressId
    ) implements Serializable{}

    public record CancelOrderRequest() implements Serializable {}

    public record OrderResponse(
            Long id,
            Long customerId,
            String customerName,
            Long shippingAddressId,
            Instant orderDate,
            Double total,
            OrderStatus status,
            List<OrderItemDTO.OrderItemResponse> items
    ) implements Serializable{}
}
