package com.unimag.ecomerce.services.mappers;

import com.unimag.ecomerce.dto.OrderDTO;
import com.unimag.ecomerce.entities.Address;
import com.unimag.ecomerce.entities.Customer;
import com.unimag.ecomerce.entities.Order;
import com.unimag.ecomerce.enums.OrderStatus;
import com.unimag.ecomerce.mappers.OrderMapper;
import org.mapstruct.factory.Mappers;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import static org.assertj.core.api.Assertions.assertThat;

public class OrderMapperTest {

    private final OrderMapper mapper = Mappers.getMapper(OrderMapper.class);

    @Test
    void toDTO_ShouldMapFields() {
        Customer customer = Customer.builder()
                .id(1L)
                .build();

        Address address = Address.builder()
                .id(2L)
                .build();

        Order order = Order.builder()
                .id(10L)
                .customer(customer)
                .shippingAddress(address)
                .orderDate(Instant.now())
                .total(50000.0)
                .status(OrderStatus.CREATED)
                .build();

        OrderDTO.OrderResponse response = mapper.toDTO(order);

        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.customerId()).isEqualTo(1L);
        assertThat(response.shippingAddressId()).isEqualTo(2L);
        assertThat(response.total()).isEqualTo(50000.0);
        assertThat(response.status()).isEqualTo(OrderStatus.CREATED);
        assertThat(response.orderDate()).isEqualTo(order.getOrderDate());
    }

}
