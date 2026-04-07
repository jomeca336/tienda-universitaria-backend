package com.unimag.ecomerce.repositories;

import com.unimag.ecomerce.entities.*;
import com.unimag.ecomerce.enums.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrderRepositoryIntegrationTest extends AbstractRepositoryIT {

    @Autowired
    private OrderRepository orderRepo;

    @Autowired
    private OrderItemRepository orderItemRepo;

    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private AddressRepository addressRepo;

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private CategoryRepository categoryRepo;

    @Test
    @DisplayName("Debe encontrar pedidos aplicando filtros compuestos de cliente, estado, fechas y rangos de coste")
    void shouldFindOrdersByFilters() {
        Customer c1 = Customer.builder().name("John Doe").email("john@mail.com").build();
        customerRepo.save(c1);

        Address a1 = Address.builder().addressLine("Main St").city("Springfield").customer(c1).build();
        addressRepo.save(a1);

        Order ord = Order.builder()
                .customer(c1)
                .shippingAddress(a1)
                .orderDate(Instant.now())
                .total(150.0)
                .status(OrderStatus.SHIPPED)
                .build();
        orderRepo.save(ord);

        Instant startDate = Instant.now().minus(2, ChronoUnit.DAYS);
        Instant endDate = Instant.now().plus(2, ChronoUnit.DAYS);

        // Act
        List<Order> result = orderRepo.findOrdersByFilters(c1.getId(), OrderStatus.SHIPPED, startDate, endDate, 100.0, 200.0);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTotal()).isEqualTo(150.0);
    }

    @Test
    @DisplayName("Debe agrupar y calcular los ingresos mensuales de todos los pedidos pagados")
    void shouldCalculateMonthlyIncome() {
        Customer c1 = Customer.builder().name("Jane Doe").email("jane@mail.com").build();
        customerRepo.save(c1);
        Address a1 = Address.builder().addressLine("2nd St").city("Springfield").customer(c1).build();
        addressRepo.save(a1);

        Order ord1 = Order.builder().customer(c1).shippingAddress(a1).orderDate(Instant.now()).total(200.0).status(OrderStatus.PAID).build();
        Order ord2 = Order.builder().customer(c1).shippingAddress(a1).orderDate(Instant.now()).total(300.0).status(OrderStatus.PAID).build();
        // Este no entra porque está CANCELLED
        Order ord3 = Order.builder().customer(c1).shippingAddress(a1).orderDate(Instant.now()).total(500.0).status(OrderStatus.CANCELLED).build();
        
        orderRepo.saveAll(List.of(ord1, ord2, ord3));

        List<Object[]> income = orderRepo.getMonthlyIncome();

        assertThat(income).isNotEmpty();
        // Verifica que la suma total sea 500 (200 + 300)
        assertThat((Double) income.get(0)[1]).isEqualTo(500.0);
    }

    @Test
    @DisplayName("Debe encontrar los top productos vendidos")
    void shouldFindTopSellingProducts() {
        Category cat = new Category();
        cat.setName("Games");
        categoryRepo.save(cat);

        Product p1 = new Product(); p1.setSku("GAM-1"); p1.setName("Game A"); p1.setPrice(10.0); p1.setActive(true); p1.setCategory(cat);
        Product p2 = new Product(); p2.setSku("GAM-2"); p2.setName("Game B"); p2.setPrice(20.0); p2.setActive(true); p2.setCategory(cat);
        productRepo.saveAll(List.of(p1, p2));

        Customer c1 = Customer.builder().name("Bob Sponge").email("bob@mail.com").build();
        customerRepo.save(c1);
        Address a1 = Address.builder().addressLine("Bikini").city("Bottom").customer(c1).build();
        addressRepo.save(a1);

        Order ord = Order.builder().customer(c1).shippingAddress(a1).orderDate(Instant.now()).total(100.0).status(OrderStatus.PAID).build();
        orderRepo.save(ord);

        // Product 1 sold 5
        OrderItem item1 = OrderItem.builder().order(ord).product(p1).quantity(5).unitPrice(10.0).subtotal(50.0).build();
        // Product 2 sold 10
        OrderItem item2 = OrderItem.builder().order(ord).product(p2).quantity(10).unitPrice(20.0).subtotal(200.0).build();
        orderItemRepo.saveAll(List.of(item1, item2));

        List<Object[]> topSelling = orderItemRepo.findTopSellingProducts();

        assertThat(topSelling).hasSize(2);
        // El primer lugar lo debe tener p2 (Game B, cantidad 10)
        assertThat(((Product) topSelling.get(0)[0]).getName()).isEqualTo("Game B");
        assertThat((Long) topSelling.get(0)[1]).isEqualTo(10L);

        // El segundo p1 (Game A, cantidad 5)
        assertThat(((Product) topSelling.get(1)[0]).getName()).isEqualTo("Game A");
        assertThat((Long) topSelling.get(1)[1]).isEqualTo(5L);
    }
}
