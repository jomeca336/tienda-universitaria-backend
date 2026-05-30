package com.unimag.ecomerce.services;

import com.unimag.ecomerce.api.dto.OrderDTO;
import com.unimag.ecomerce.api.dto.OrderItemDTO;
import com.unimag.ecomerce.domine.entities.*;
import com.unimag.ecomerce.enums.CustomerStatus;
import com.unimag.ecomerce.enums.OrderStatus;
import com.unimag.ecomerce.exception.BusinessException;
import com.unimag.ecomerce.exception.ResourceNotFoundException;
import com.unimag.ecomerce.exception.ValidationException;
import com.unimag.ecomerce.services.mappers.OrderMapper;
import com.unimag.ecomerce.domine.repositories.AddressRepository;
import com.unimag.ecomerce.domine.repositories.CustomerRepository;
import com.unimag.ecomerce.domine.repositories.InventoryRepository;
import com.unimag.ecomerce.domine.repositories.OrderItemRepository;
import com.unimag.ecomerce.domine.repositories.OrderRepository;
import com.unimag.ecomerce.domine.repositories.OrderStatusHistoryRepository;
import com.unimag.ecomerce.domine.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository repository;
    private final OrderItemRepository orderItemRepository;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final InventoryRepository inventoryRepository;
    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final OrderMapper mapper;

    @Override
    public OrderDTO.OrderResponse create(OrderDTO.CreateOrderRequest request) {
        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + request.customerId()));

        // Regla 6.2: el cliente debe estar activo
        if (customer.getStatus() != CustomerStatus.ACTIVE) {
            throw new BusinessException("El cliente con id " + request.customerId() + " no está activo");
        }

        Address address = addressRepository.findById(request.shippingAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + request.shippingAddressId()));

        // Regla 6.2: la dirección debe pertenecer al cliente
        if (address.getCustomer() == null || !address.getCustomer().getId().equals(customer.getId())) {
            throw new BusinessException("La dirección no pertenece al cliente con id: " + request.customerId());
        }

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
            throw new ValidationException("La cantidad debe ser mayor a cero");
        }
        Order order = getOrderById(orderId);
        if (order.getStatus() != OrderStatus.CREATED) {
            throw new BusinessException("Solo se pueden agregar ítems a pedidos en estado CREATED");
        }
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + request.productId()));

        // el producto debe estar activo
        if (product.getActive() == null || !product.getActive()) {
            throw new BusinessException("El producto con id " + request.productId() + " no está activo");
        }

        // reservar stock inmediatamente al agregar el ítem
        Inventory inventory = inventoryRepository.findByProductId(product.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product: " + product.getId()));
        if (inventory.getStock() < request.quantity()) {
            throw new BusinessException("Stock insuficiente para \"" + product.getName() + "\". Disponible: " + inventory.getStock());
        }
        inventory.setStock(inventory.getStock() - request.quantity());
        inventoryRepository.save(inventory);

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
        Order order = getOrderById(orderId);

        // Regla 6.3: solo CREATED puede pasar a PAID
        if (order.getStatus() != OrderStatus.CREATED) {
            throw new BusinessException("Solo se pueden pagar pedidos en estado CREATED");
        }
        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new BusinessException("No se puede pagar un pedido sin ítems");
        }

        // stock ya fue descontado al agregar los ítems
        saveStatusHistory(order, order.getStatus(), OrderStatus.PAID);
        order.setStatus(OrderStatus.PAID);
        return mapper.toDTO(repository.save(order));
    }

    @Override
    public OrderDTO.OrderResponse ship(Long orderId) {
        Order order = getOrderById(orderId);

        // Regla 6.4: solo PAID puede pasar a SHIPPED
        if (order.getStatus() != OrderStatus.PAID) {
            throw new BusinessException("Solo se pueden despachar pedidos en estado PAID");
        }

        saveStatusHistory(order, order.getStatus(), OrderStatus.SHIPPED);
        order.setStatus(OrderStatus.SHIPPED);
        return mapper.toDTO(repository.save(order));
    }

    @Override
    public OrderDTO.OrderResponse deliver(Long orderId) {
        Order order = getOrderById(orderId);

        // Regla 6.4: solo SHIPPED puede pasar a DELIVERED
        if (order.getStatus() != OrderStatus.SHIPPED) {
            throw new BusinessException("Solo se pueden entregar pedidos en estado SHIPPED");
        }

        saveStatusHistory(order, order.getStatus(), OrderStatus.DELIVERED);
        order.setStatus(OrderStatus.DELIVERED);
        return mapper.toDTO(repository.save(order));
    }

    @Override
    public OrderDTO.OrderResponse cancel(Long orderId) {
        Order order = getOrderById(orderId);

        // Regla 6.5: SHIPPED, DELIVERED y ya CANCELLED no pueden cancelarse
        if (order.getStatus() == OrderStatus.SHIPPED
                || order.getStatus() == OrderStatus.DELIVERED
                || order.getStatus() == OrderStatus.CANCELLED) {
            throw new BusinessException("El pedido no puede cancelarse en estado: " + order.getStatus());
        }

        // devolver stock al cancelar (fue reservado al agregar ítems)
        if (order.getItems() != null) {
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
        return mapper.toDTO(getOrderById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDTO.OrderResponse> list(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toDTO);
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Order not found with id: " + id);
        }
        repository.deleteById(id);
    }

    private Order getOrderById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
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
}
