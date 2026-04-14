package com.unimag.ecomerce.services;

import com.unimag.ecomerce.api.dto.OrderDTO.*;
import com.unimag.ecomerce.api.dto.OrderItemDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    OrderResponse create(CreateOrderRequest request);
    OrderResponse get(Long id);
    Page<OrderResponse> list(Pageable pageable);

    OrderResponse addItem(Long orderId, OrderItemDTO.CreateOrderItemRequest request);
    OrderResponse pay(Long orderId);
    OrderResponse ship(Long orderId);
    OrderResponse deliver(Long orderId);
    OrderResponse cancel(Long orderId);

    void delete(Long id);
}
