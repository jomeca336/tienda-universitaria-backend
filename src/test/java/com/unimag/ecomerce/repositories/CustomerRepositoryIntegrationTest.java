package com.unimag.ecomerce.repositories;

import com.unimag.ecomerce.entities.*;
import com.unimag.ecomerce.enums.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerRepositoryIntegrationTest extends AbstractRepositoryIT {

    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private AddressRepository addressRepo;

    @Autowired
    private OrderRepository orderRepo;

    @Test
    @DisplayName("Debe encontrar los clientes con mayor facturación")
    void shouldFindTopCustomers() {
        // Arrange
        Customer c1 = Customer.builder().name("Alice Smith").email("alice@mail.com").build();
        Customer c2 = Customer.builder().name("Bob Jones").email("bob@mail.com").build();
        customerRepo.saveAll(List.of(c1, c2));

        Address a1 = Address.builder().addressline("St 1").city("City 1").customer(c1).build();
        Address a2 = Address.builder().addressline("St 2").city("City 2").customer(c2).build();
        addressRepo.saveAll(List.of(a1, a2));

        // Alice compró 50
        Order o1 = Order.builder().customer(c1).shippingAddress(a1).orderDate(Instant.now()).total(50.0).status(OrderStatus.PAID).build();
        // Bob compró 100 + 200 = 300
        Order o2 = Order.builder().customer(c2).shippingAddress(a2).orderDate(Instant.now()).total(100.0).status(OrderStatus.PAID).build();
        Order o3 = Order.builder().customer(c2).shippingAddress(a2).orderDate(Instant.now()).total(200.0).status(OrderStatus.DELIVERED).build();

        orderRepo.saveAll(List.of(o1, o2, o3));

        // Act
        List<Object[]> topCustomers = customerRepo.findTopCustomers();

        // Assert
        assertThat(topCustomers).hasSize(2);
        // Bob gastó 300
        assertThat(((Customer) topCustomers.get(0)[0]).getName()).isEqualTo("Bob Jones");
        assertThat((Double) topCustomers.get(0)[1]).isEqualTo(300.0);
        // Alice gastó 50
        assertThat(((Customer) topCustomers.get(1)[0]).getName()).isEqualTo("Alice Smith");
        assertThat((Double) topCustomers.get(1)[1]).isEqualTo(50.0);
    }
}
