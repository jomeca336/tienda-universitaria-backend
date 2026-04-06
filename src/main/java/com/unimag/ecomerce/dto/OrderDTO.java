package com.unimag.ecomerce.dto;


import com.unimag.ecomerce.enums.OrderStatus;

import java.io.Serializable;
import java.time.Instant;

public class OrderDTO {

    public record CreateOrderRequest(
        Long customerId,
        Long shippingAddressId
    ) implements Serializable{}

    public record CancelOrder() implements Serializable {}

    public record OrderResponse(
            Long id,
            Long customerId,
            Long shippingAddressId,
            Instant orderDate,
            Double total,
            OrderStatus status
    ) implements Serializable{}
}
