package com.unimag.ecomerce.Services;

import com.unimag.ecomerce.dto.OrderDTO;
import com.unimag.ecomerce.dto.OrderItemDTO;
import com.unimag.ecomerce.entities.Order;

import java.util.List;

public interface OrderService {

    OrderDTO.OrderResponse create(OrderDTO.CreateOrderRequest request);
    OrderDTO.OrderResponse addItem(Long orderId, OrderItemDTO.CreateOrderItemRequest request);
    OrderDTO.OrderResponse pay(Long orderId);
    OrderDTO.OrderResponse cancel(Long orderId);
    OrderDTO.OrderResponse get(Long id);
    Order getObjectById(Long id);
    List<OrderDTO.OrderResponse> list();
}
