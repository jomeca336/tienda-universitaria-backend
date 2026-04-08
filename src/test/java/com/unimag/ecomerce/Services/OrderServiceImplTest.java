package com.unimag.ecomerce.Services;

import com.unimag.ecomerce.dto.OrderDTO;
import com.unimag.ecomerce.dto.OrderItemDTO;
import com.unimag.ecomerce.entities.*;
import com.unimag.ecomerce.enums.OrderStatus;
import com.unimag.ecomerce.exception.NotFoundException;
import com.unimag.ecomerce.mappers.OrderMapper;
import com.unimag.ecomerce.repositories.InventoryRepository;
import com.unimag.ecomerce.repositories.OrderItemRepository;
import com.unimag.ecomerce.repositories.OrderRepository;
import com.unimag.ecomerce.repositories.OrderStatusHistoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock private OrderRepository orderRepository;
    @Mock private OrderItemRepository orderItemRepository;
    @Mock private OrderStatusHistoryRepository orderStatusHistoryRepository;
    @Mock private InventoryRepository inventoryRepository;
    @Mock private CustomerService customerService;
    @Mock private AddressService addressService;
    @Mock private ProductService productService;
    @Mock private OrderMapper orderMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    // ─── create ───────────────────────────────────────────────────────────────

    @Test
    void create_ShouldCreateOrder_WithCreatedStatusAndZeroTotal() {
        var request = new OrderDTO.CreateOrderRequest(1L, 2L);
        Customer customer = Customer.builder().id(1L).build();
        Address address = Address.builder().id(2L).build();
        Order saved = Order.builder()
                .id(10L).customer(customer).shippingAddress(address)
                .total(0.0).status(OrderStatus.CREATED).orderDate(Instant.now())
                .build();
        var expectedResponse = new OrderDTO.OrderResponse(10L, 1L, 2L, saved.getOrderDate(), 0.0, OrderStatus.CREATED);

        when(customerService.getObjectById(1L)).thenReturn(customer);
        when(addressService.getObjectById(2L)).thenReturn(address);
        when(orderRepository.save(any())).thenReturn(saved);
        when(orderMapper.toDTO(saved)).thenReturn(expectedResponse);

        OrderDTO.OrderResponse response = orderService.create(request);

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(captor.getValue().getTotal()).isEqualTo(0.0);
        assertThat(response.status()).isEqualTo(OrderStatus.CREATED);
    }

    // ─── addItem: validación de cantidad inválida ──────────────────────────────

    @Test
    void addItem_ShouldThrow_WhenQuantityIsZero() {
        var request = new OrderItemDTO.CreateOrderItemRequest(1L, 0);

        assertThatThrownBy(() -> orderService.addItem(1L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("mayor a cero");
    }

    @Test
    void addItem_ShouldThrow_WhenQuantityIsNegative() {
        var request = new OrderItemDTO.CreateOrderItemRequest(1L, -3);

        assertThatThrownBy(() -> orderService.addItem(1L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("mayor a cero");
    }

    // ─── addItem: transición de estado ────────────────────────────────────────

    @Test
    void addItem_ShouldThrow_WhenOrderStatusIsNotCreated() {
        Order order = Order.builder().id(1L).status(OrderStatus.PAID).total(0.0).build();
        var request = new OrderItemDTO.CreateOrderItemRequest(2L, 1);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.addItem(1L, request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("CREATED");
    }

    // ─── addItem: cálculo correcto de subtotal y total ────────────────────────

    @Test
    void addItem_ShouldCalculateSubtotalAndTotalCorrectly() {
        Product product = Product.builder().id(2L).price(5000.0).build();
        Order order = Order.builder().id(1L).status(OrderStatus.CREATED).total(10000.0).build();
        var request = new OrderItemDTO.CreateOrderItemRequest(2L, 3);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productService.getObjectById(2L)).thenReturn(product);
        when(orderItemRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(orderMapper.toDTO(any())).thenReturn(mock(OrderDTO.OrderResponse.class));

        orderService.addItem(1L, request);

        // Verificar total del pedido: 10000 + (3 * 5000) = 25000
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());
        assertThat(orderCaptor.getValue().getTotal()).isEqualTo(25000.0);

        // Verificar subtotal del ítem: 3 * 5000 = 15000
        ArgumentCaptor<OrderItem> itemCaptor = ArgumentCaptor.forClass(OrderItem.class);
        verify(orderItemRepository).save(itemCaptor.capture());
        assertThat(itemCaptor.getValue().getUnitPrice()).isEqualTo(5000.0);
        assertThat(itemCaptor.getValue().getSubtotal()).isEqualTo(15000.0);
    }

    @Test
    void addItem_ShouldNotTouchInventory_WhenItemAdded() {
        Product product = Product.builder().id(2L).price(1000.0).build();
        Order order = Order.builder().id(1L).status(OrderStatus.CREATED).total(0.0).build();
        var request = new OrderItemDTO.CreateOrderItemRequest(2L, 2);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productService.getObjectById(2L)).thenReturn(product);
        when(orderItemRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(orderMapper.toDTO(any())).thenReturn(mock(OrderDTO.OrderResponse.class));

        orderService.addItem(1L, request);

        verifyNoInteractions(inventoryRepository);
    }

    // ─── pay: descuento de inventario ─────────────────────────────────────────

    @Test
    void pay_ShouldDecrementInventoryStock_WhenOrderPaid() {
        Inventory inventory = Inventory.builder().id(1L).stock(10).minStock(0).build();
        Product product = Product.builder().id(2L).price(5000.0).build();
        product.setInventory(inventory);
        OrderItem item = OrderItem.builder().product(product).quantity(3).build();
        Order order = Order.builder().id(1L).status(OrderStatus.CREATED)
                .items(new ArrayList<>(List.of(item))).build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(inventoryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(orderStatusHistoryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(orderMapper.toDTO(any())).thenReturn(mock(OrderDTO.OrderResponse.class));

        orderService.pay(1L);

        ArgumentCaptor<Inventory> inventoryCaptor = ArgumentCaptor.forClass(Inventory.class);
        verify(inventoryRepository).save(inventoryCaptor.capture());
        assertThat(inventoryCaptor.getValue().getStock()).isEqualTo(7); // 10 - 3
    }

    @Test
    void pay_ShouldThrow_WhenInsufficientStock() {
        Inventory inventory = Inventory.builder().id(1L).stock(2).minStock(0).build();
        Product product = Product.builder().id(2L).name("Sudadera").price(5000.0).build();
        product.setInventory(inventory);
        OrderItem item = OrderItem.builder().product(product).quantity(5).build();
        Order order = Order.builder().id(1L).status(OrderStatus.CREATED)
                .items(new ArrayList<>(List.of(item))).build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.pay(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Stock insuficiente");
    }

    @Test
    void pay_ShouldThrow_WhenOrderHasNoItems() {
        Order order = Order.builder().id(1L).status(OrderStatus.CREATED)
                .items(new ArrayList<>()).build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.pay(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("sin ítems");
    }

    @Test
    void pay_ShouldThrow_WhenOrderStatusIsNotCreated() {
        Order order = Order.builder().id(1L).status(OrderStatus.PAID).build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.pay(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("CREATED");
    }

    @Test
    void pay_ShouldSaveStatusHistory_WhenOrderPaid() {
        Inventory inventory = Inventory.builder().id(1L).stock(5).minStock(0).build();
        Product product = Product.builder().id(2L).price(1000.0).build();
        product.setInventory(inventory);
        OrderItem item = OrderItem.builder().product(product).quantity(1).build();
        Order order = Order.builder().id(1L).status(OrderStatus.CREATED)
                .items(new ArrayList<>(List.of(item))).build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(inventoryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(orderStatusHistoryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(orderMapper.toDTO(any())).thenReturn(mock(OrderDTO.OrderResponse.class));

        orderService.pay(1L);

        ArgumentCaptor<OrderStatusHistory> historyCaptor = ArgumentCaptor.forClass(OrderStatusHistory.class);
        verify(orderStatusHistoryRepository).save(historyCaptor.capture());
        assertThat(historyCaptor.getValue().getPreviousStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(historyCaptor.getValue().getNewStatus()).isEqualTo(OrderStatus.PAID);
    }

    // ─── cancel: reversión de stock solo si era PAID ──────────────────────────

    @Test
    void cancel_ShouldRestoreInventoryStock_WhenOrderWasPaid() {
        Inventory inventory = Inventory.builder().id(1L).stock(5).minStock(0).build();
        Product product = Product.builder().id(2L).price(1000.0).build();
        product.setInventory(inventory);
        OrderItem item = OrderItem.builder().id(1L).product(product).quantity(3).build();
        Order order = Order.builder().id(1L).status(OrderStatus.PAID)
                .total(3000.0).items(new ArrayList<>(List.of(item))).build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderStatusHistoryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(inventoryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(orderMapper.toDTO(any())).thenReturn(mock(OrderDTO.OrderResponse.class));

        orderService.cancel(1L);

        ArgumentCaptor<Inventory> inventoryCaptor = ArgumentCaptor.forClass(Inventory.class);
        verify(inventoryRepository).save(inventoryCaptor.capture());
        assertThat(inventoryCaptor.getValue().getStock()).isEqualTo(8); // 5 + 3
    }

    @Test
    void cancel_ShouldNotRestoreStock_WhenOrderWasCreated() {
        Order order = Order.builder().id(1L).status(OrderStatus.CREATED)
                .items(new ArrayList<>()).build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderStatusHistoryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(orderMapper.toDTO(any())).thenReturn(mock(OrderDTO.OrderResponse.class));

        orderService.cancel(1L);

        verifyNoInteractions(inventoryRepository);
    }

    // ─── cancel: validación de transiciones de estado ─────────────────────────

    @Test
    void cancel_ShouldThrow_WhenOrderIsAlreadyCancelled() {
        Order order = Order.builder().id(1L).status(OrderStatus.CANCELLED).build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.cancel(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("CANCELLED");
    }

    @Test
    void cancel_ShouldThrow_WhenOrderIsDelivered() {
        Order order = Order.builder().id(1L).status(OrderStatus.DELIVERED).build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.cancel(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("DELIVERED");
    }

    // ─── getObjectById ────────────────────────────────────────────────────────

    @Test
    void getObjectById_ShouldThrow_WhenOrderNotFound() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getObjectById(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("99");
    }
}
