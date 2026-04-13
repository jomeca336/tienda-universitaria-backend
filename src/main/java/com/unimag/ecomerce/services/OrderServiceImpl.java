package com.unimag.ecomerce.services;

import com.unimag.ecomerce.api.dto.OrderDTO;
import com.unimag.ecomerce.api.dto.OrderItemDTO;
import com.unimag.ecomerce.domine.entities.*;
import com.unimag.ecomerce.enums.OrderStatus;
import com.unimag.ecomerce.exception.NotFoundException;
import com.unimag.ecomerce.services.mappers.OrderMapper;
import com.unimag.ecomerce.domine.repositories.InventoryRepository;
import com.unimag.ecomerce.domine.repositories.OrderItemRepository;
import com.unimag.ecomerce.domine.repositories.OrderRepository;
import com.unimag.ecomerce.domine.repositories.OrderStatusHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository repository;
    private final OrderItemRepository orderItemRepository;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final InventoryRepository inventoryRepository;
    private final CustomerService customerService;
    private final AddressService addressService;
    private final ProductService productService;
    private final OrderMapper mapper;

    @Override
    public OrderDTO.OrderResponse create(OrderDTO.CreateOrderRequest request) {
        Customer customer = customerService.getObjectById(request.customerId());
        Address address = addressService.getObjectById(request.shippingAddressId());
        Order order = Order.builder()
                .customer(customer)
                .shippingAddress(address)
                .orderDate(Instant.now())
                .total(0.0)
                .status(OrderStatus.CREATED)
                .build();
        return mapper.toDTO(repository.save(order));
    }

    @Override
    public OrderDTO.OrderResponse addItem(Long orderId, OrderItemDTO.CreateOrderItemRequest request) {
        if (request.quantity() == null || request.quantity() <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }
        Order order = getObjectById(orderId);
        if (order.getStatus() != OrderStatus.CREATED) {
            throw new IllegalStateException("Solo se pueden agregar ítems a pedidos en estado CREATED");
        }
        Product product = productService.getObjectById(request.productId());
        double unitPrice = product.getPrice();
        double subtotal = unitPrice * request.quantity();
        OrderItem item = OrderItem.builder()
                .order(order)
                .product(product)
                .quantity(request.quantity())
                .unitPrice(unitPrice)
                .subtotal(subtotal)
                .build();
        orderItemRepository.save(item);
        double currentTotal = order.getTotal() != null ? order.getTotal() : 0.0;
        order.setTotal(currentTotal + subtotal);
        return mapper.toDTO(repository.save(order));
    }

    @Override
    public OrderDTO.OrderResponse pay(Long orderId) {
        Order order = getObjectById(orderId);
        if (order.getStatus() != OrderStatus.CREATED) {
            throw new IllegalStateException("Solo se pueden pagar pedidos en estado CREATED");
        }
        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new IllegalStateException("No se puede pagar un pedido sin ítems");
        }
        for (OrderItem item : order.getItems()) {
            Inventory inventory = item.getProduct().getInventory();
            if (inventory == null || inventory.getStock() < item.getQuantity()) {
                throw new IllegalStateException(
                        "Stock insuficiente para el producto: " + item.getProduct().getName());
            }
        }
        for (OrderItem item : order.getItems()) {
            Inventory inventory = item.getProduct().getInventory();
            inventory.setStock(inventory.getStock() - item.getQuantity());
            inventoryRepository.save(inventory);
        }
        saveStatusHistory(order, order.getStatus(), OrderStatus.PAID);
        order.setStatus(OrderStatus.PAID);
        return mapper.toDTO(repository.save(order));
    }


    @Override
    public OrderDTO.OrderResponse cancel(Long orderId) {
        Order order = getObjectById(orderId);
        if (order.getStatus() == OrderStatus.CANCELLED || order.getStatus() == OrderStatus.DELIVERED) {
            throw new IllegalStateException("El pedido no puede cancelarse en estado: " + order.getStatus());
        }
        if (order.getStatus() == OrderStatus.PAID && order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                Inventory inventory = item.getProduct().getInventory();
                if (inventory != null) {
                    inventory.setStock(inventory.getStock() + item.getQuantity());
                    inventoryRepository.save(inventory);
                }
            }
        }
        saveStatusHistory(order, order.getStatus(), OrderStatus.CANCELLED);
        order.setStatus(OrderStatus.CANCELLED);
        return mapper.toDTO(repository.save(order));
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDTO.OrderResponse get(Long id) {
        return mapper.toDTO(getObjectById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Order getObjectById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDTO.OrderResponse> list(Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::toDTO);
    }

    private void saveStatusHistory(Order order, OrderStatus previous, OrderStatus next) {
        OrderStatusHistory history = OrderStatusHistory.builder()
                .order(order)
                .previousStatus(previous)
                .newStatus(next)
                .changeDate(Instant.now())
                .build();
        orderStatusHistoryRepository.save(history);
    }
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Address not found");
        }
        repository.deleteById(id);
    }
}
